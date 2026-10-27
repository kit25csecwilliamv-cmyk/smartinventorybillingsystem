const API = "";
let products = [];
let cart = [];

const money = value => new Intl.NumberFormat("en-IN", {
  style: "currency", currency: "INR", maximumFractionDigits: 2
}).format(Number(value || 0));

async function api(url, options = {}) {
  const response = await fetch(API + url, {
    headers: { "Content-Type": "application/json", ...(options.headers || {}) },
    ...options
  });
  const data = response.status === 204 ? null : await response.json().catch(() => ({}));
  if (!response.ok) throw new Error(data?.error || `Request failed (${response.status})`);
  return data;
}

function showMessage(id, message, type = "") {
  const element = document.getElementById(id);
  element.textContent = message;
  element.className = `message ${type}`;
}

async function loadDashboard() {
  try {
    const data = await api("/api/dashboard");
    document.getElementById("productCount").textContent = data.productCount;
    document.getElementById("stockUnits").textContent = data.stockUnits;
    document.getElementById("lowStockCount").textContent = data.lowStockCount;
    document.getElementById("todaySales").textContent = money(data.todaySales);
    document.getElementById("billCount").textContent = data.totalBillsToday;
  } catch (error) { console.error(error); }
}

async function loadProducts() {
  try {
    const q = document.getElementById("searchBox").value.trim();
    products = await api("/api/products" + (q ? `?q=${encodeURIComponent(q)}` : ""));
    const rows = document.getElementById("productRows");
    if (!products.length) {
      rows.innerHTML = '<tr><td colspan="6">No products found. Add your first product.</td></tr>';
    } else {
      rows.innerHTML = products.map(p => {
        const low = p.stockQuantity <= p.minStockLevel;
        return `<tr>
          <td>${escapeHtml(p.name)}</td><td>${escapeHtml(p.category)}</td>
          <td>${money(p.sellingPrice)}</td><td>${p.stockQuantity}</td>
          <td><span class="badge ${low ? "low" : "ok"}">${low ? "Low stock" : "In stock"}</span></td>
          <td><button class="action-btn" onclick="editProduct(${p.id})">Edit</button><button class="action-btn delete" onclick="deleteProduct(${p.id})">Delete</button></td>
        </tr>`;
      }).join("");
    }
    const select = document.getElementById("billProduct");
    const previous = select.value;
    select.innerHTML = '<option value="">Select product...</option>' + products
      .filter(p => p.stockQuantity > 0)
      .map(p => `<option value="${p.id}">${escapeHtml(p.name)} — ${money(p.sellingPrice)} (Stock: ${p.stockQuantity})</option>`).join("");
    if (previous) select.value = previous;
    await loadLowStock();
  } catch (error) { showMessage("productMessage", error.message, "error"); }
}

async function loadLowStock() {
  try {
    const low = await api("/api/products/low-stock");
    const rows = document.getElementById("lowStockRows");
    rows.innerHTML = low.length ? low.map(p => `<tr>
      <td>${escapeHtml(p.name)}</td><td>${escapeHtml(p.category)}</td>
      <td><span class="badge low">${p.stockQuantity}</span></td><td>${p.minStockLevel}</td>
    </tr>`).join("") : '<tr><td colspan="4">All products are above their minimum stock levels.</td></tr>';
  } catch (error) { console.error(error); }
}

function openProductForm(product = null) {
  document.getElementById("productForm").reset();
  document.getElementById("editProductId").value = product?.id || "";
  document.getElementById("productFormTitle").textContent = product ? "Edit Product" : "Add Product";
  document.getElementById("productName").value = product?.name || "";
  document.getElementById("productCategory").value = product?.category || "";
  document.getElementById("purchasePrice").value = product?.purchasePrice ?? "";
  document.getElementById("sellingPrice").value = product?.sellingPrice ?? "";
  document.getElementById("stockQuantity").value = product?.stockQuantity ?? 0;
  document.getElementById("minStockLevel").value = product?.minStockLevel ?? 5;
  showMessage("productFormMessage", "");
  document.getElementById("productDialog").showModal();
}
function closeProductForm() { document.getElementById("productDialog").close(); }
function editProduct(id) {
  const product = products.find(p => p.id === id);
  if (product) openProductForm(product);
}

document.getElementById("productForm").addEventListener("submit", async event => {
  event.preventDefault();
  const id = document.getElementById("editProductId").value;
  const payload = {
    name: document.getElementById("productName").value.trim(),
    category: document.getElementById("productCategory").value.trim(),
    purchasePrice: Number(document.getElementById("purchasePrice").value),
    sellingPrice: Number(document.getElementById("sellingPrice").value),
    stockQuantity: Number(document.getElementById("stockQuantity").value),
    minStockLevel: Number(document.getElementById("minStockLevel").value)
  };
  try {
    await api(id ? `/api/products/${id}` : "/api/products", {
      method: id ? "PUT" : "POST", body: JSON.stringify(payload)
    });
    closeProductForm();
    await refreshAll();
    showMessage("productMessage", id ? "Product updated." : "Product added.", "success");
  } catch (error) { showMessage("productFormMessage", error.message, "error"); }
});

async function deleteProduct(id) {
  const product = products.find(p => p.id === id);
  if (!confirm(`Delete "${product?.name || "this product"}"?`)) return;
  try {
    await api(`/api/products/${id}`, { method: "DELETE" });
    await refreshAll();
  } catch (error) { alert(error.message); }
}

function addToCart() {
  const id = Number(document.getElementById("billProduct").value);
  const qty = Number(document.getElementById("billQty").value);
  const product = products.find(p => p.id === id);
  if (!product) return showMessage("billMessage", "Choose a product first.", "error");
  if (!Number.isInteger(qty) || qty < 1) return showMessage("billMessage", "Quantity must be at least 1.", "error");
  const existing = cart.find(item => item.productId === id);
  const requested = qty + (existing?.quantity || 0);
  if (requested > product.stockQuantity) return showMessage("billMessage", `Only ${product.stockQuantity} unit(s) available.`, "error");
  if (existing) existing.quantity = requested;
  else cart.push({ productId: id, quantity: qty, name: product.name, price: Number(product.sellingPrice) });
  renderCart();
  showMessage("billMessage", "");
}
function removeCartItem(id) { cart = cart.filter(item => item.productId !== id); renderCart(); }
function renderCart() {
  const list = document.getElementById("cartList");
  list.innerHTML = cart.length ? cart.map(item => `<div class="cart-row">
    <div><strong>${escapeHtml(item.name)}</strong><small>${item.quantity} × ${money(item.price)}</small></div>
    <div><strong>${money(item.quantity * item.price)}</strong> <button class="remove-cart" onclick="removeCartItem(${item.productId})">×</button></div>
  </div>`).join("") : '<p class="muted">No items added yet.</p>';
  document.getElementById("cartTotal").textContent = money(cart.reduce((sum, item) => sum + item.quantity * item.price, 0));
}

document.getElementById("billForm").addEventListener("submit", async event => {
  event.preventDefault();
  if (!cart.length) return showMessage("billMessage", "Add at least one item to the bill.", "error");
  try {
    const sale = await api("/api/sales", {
      method: "POST",
      body: JSON.stringify({
        customerName: document.getElementById("customerName").value.trim(),
        items: cart.map(item => ({ productId: item.productId, quantity: item.quantity }))
      })
    });
    showMessage("billMessage", `Bill #${sale.id} saved successfully. Total: ${money(sale.totalAmount)}`, "success");
    cart = [];
    document.getElementById("billForm").reset();
    renderCart();
    await refreshAll();
  } catch (error) { showMessage("billMessage", error.message, "error"); }
});

async function loadSales() {
  try {
    const sales = await api("/api/sales");
    const rows = document.getElementById("salesRows");
    rows.innerHTML = sales.length ? sales.slice(0, 10).map(s => `<tr>
      <td>#${s.id}</td><td>${escapeHtml(s.customerName)}</td>
      <td>${new Date(s.saleDate).toLocaleString("en-IN")}</td>
      <td>${(s.items || []).reduce((sum, item) => sum + item.quantity, 0)} units</td>
      <td><strong>${money(s.totalAmount)}</strong></td>
    </tr>`).join("") : '<tr><td colspan="5">No sales recorded yet.</td></tr>';
  } catch (error) { console.error(error); }
}

function escapeHtml(value) {
  return String(value ?? "").replace(/[&<>"']/g, char => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#039;"
  })[char]);
}
async function refreshAll() {
  await Promise.all([loadDashboard(), loadProducts(), loadSales()]);
}
document.getElementById("todayDate").textContent = new Date().toLocaleDateString("en-IN", {
  weekday: "short", day: "2-digit", month: "short", year: "numeric"
});
refreshAll();
