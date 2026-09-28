/**
 * FarmStock - Frontend Application Logic
 * Vanilla JS SPA connected to Spring Boot REST APIs
 */

const API_BASE = '/api';
const STATE = {
    crops: [],
    harvests: [],
    sales: [],
    stock: []
};

// --- INITIALIZATION ---
document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    initModals();
    initForms();
    initDate();
    
    // Initial data load
    refreshAllData();

    // Refresh button
    document.getElementById('btn-refresh').addEventListener('click', refreshAllData);
});

// --- NAVIGATION & UI STATE ---
function initNavigation() {
    const links = document.querySelectorAll('.nav-link');
    const views = document.querySelectorAll('.view');
    const pageTitle = document.getElementById('page-title');
    const mobileBtn = document.getElementById('mobile-menu-btn');
    const sidebar = document.getElementById('sidebar');

    links.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const targetId = link.getAttribute('data-target');
            
            // Update active link
            links.forEach(l => l.classList.remove('active'));
            link.classList.add('active');

            // Update title
            pageTitle.innerText = link.innerText;

            // Show target view
            views.forEach(v => {
                if (v.id === targetId) v.classList.add('active');
                else v.classList.remove('active');
            });

            // Close sidebar on mobile
            if (window.innerWidth <= 768) {
                sidebar.classList.remove('open');
            }
        });
    });

    mobileBtn.addEventListener('click', () => {
        sidebar.classList.toggle('open');
    });
}

function initDate() {
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    document.getElementById('current-date').innerText = new Date().toLocaleDateString('en-US', options);
}

// --- MODALS ---
function initModals() {
    // Close modals on outside click
    document.querySelectorAll('.modal-overlay').forEach(overlay => {
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) {
                closeModal(overlay.id);
            }
        });
    });
}

function openModal(id) {
    document.getElementById(id).classList.add('active');
    
    // Auto-focus first input if exists
    setTimeout(() => {
        const firstInput = document.querySelector(`#${id} input:not([type=hidden]), #${id} select`);
        if (firstInput) firstInput.focus();
    }, 100);
}

function closeModal(id) {
    document.getElementById(id).classList.remove('active');
    // Reset forms inside the modal
    const form = document.querySelector(`#${id} form`);
    if (form) {
        form.reset();
        const hiddenId = form.querySelector('input[type="hidden"]');
        if (hiddenId) hiddenId.value = '';
    }
}

// --- TOASTS ---
function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    const icon = type === 'success' ? '<i class="fa-solid fa-circle-check success"></i>' : '<i class="fa-solid fa-circle-exclamation error"></i>';
    toast.innerHTML = `${icon} <span>${message}</span>`;
    
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.classList.add('fade-out');
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

function handleError(err) {
    console.error(err);
    const msg = err.message || err.error || "An unexpected error occurred.";
    showToast(msg, 'error');
}

// --- API FETCHING ---
async function fetchAPI(endpoint, options = {}) {
    try {
        const res = await fetch(`${API_BASE}${endpoint}`, options);
        if (res.status === 204) return true; // No content
        
        const data = await res.json();
        if (!res.ok) throw data;
        return data;
    } catch (err) {
        if (err instanceof TypeError) throw { message: 'Unable to connect to the server.' };
        throw err;
    }
}

async function refreshAllData() {
    try {
        await Promise.all([
            loadCrops(),
            loadStock(),
            loadHarvests(),
            loadSales()
        ]);
        updateDashboardCards();
        updateRecentActivity();
        populateDropdowns();
    } catch (err) {
        handleError(err);
    }
}

// --- CROPS ---
async function loadCrops() {
    STATE.crops = await fetchAPI('/crops');
    renderCropsTable();
}

function renderCropsTable() {
    let filteredCrops = STATE.crops;
    const search = document.getElementById('search-crop').value.toLowerCase();
    if (search) {
        filteredCrops = filteredCrops.filter(c => c.name.toLowerCase().includes(search) || c.category.toLowerCase().includes(search));
    }

    const tbody = document.querySelector('#crops-table tbody');
    const emptyState = document.getElementById('crops-empty');
    
    tbody.innerHTML = '';
    
    if (filteredCrops.length === 0) {
        emptyState.classList.remove('hidden');
        document.getElementById('crops-table').classList.add('hidden');
    } else {
        emptyState.classList.add('hidden');
        document.getElementById('crops-table').classList.remove('hidden');
        
        filteredCrops.forEach(c => {
            tbody.innerHTML += `
                <tr>
                    <td>${c.id}</td>
                    <td><strong>${c.name}</strong></td>
                    <td>${c.category}</td>
                    <td class="text-right table-actions">
                        <button class="btn-icon-only edit" onclick="editCrop(${c.id})" title="Edit"><i class="fa-solid fa-pencil"></i></button>
                        <button class="btn-icon-only delete" onclick="confirmDelete('crop', ${c.id}, '${c.name.replace(/'/g, "\\'").replace(/"/g, '&quot;')}')" title="Delete"><i class="fa-solid fa-trash"></i></button>
                    </td>
                </tr>
            `;
        });
    }
}

document.getElementById('search-crop').addEventListener('input', renderCropsTable);

// --- HARVESTS ---
async function loadHarvests() {
    STATE.harvests = await fetchAPI('/harvests');
    renderHarvestsTable();
}

function renderHarvestsTable() {
    const tbody = document.querySelector('#harvests-table tbody');
    const emptyState = document.getElementById('harvests-empty');
    
    tbody.innerHTML = '';
    
    if (STATE.harvests.length === 0) {
        emptyState.classList.remove('hidden');
        document.getElementById('harvests-table').classList.add('hidden');
    } else {
        emptyState.classList.add('hidden');
        document.getElementById('harvests-table').classList.remove('hidden');
        
        STATE.harvests.forEach(h => {
            tbody.innerHTML += `
                <tr>
                    <td>${h.id}</td>
                    <td><strong>${h.cropName}</strong></td>
                    <td>${h.quantity} kg</td>
                    <td>${h.harvestDate}</td>
                    <td class="text-right table-actions">
                        <button class="btn-icon-only delete" onclick="confirmDelete('harvests', ${h.id}, '${h.cropName.replace(/'/g, "\\'").replace(/"/g, '&quot;')} harvest')" title="Delete"><i class="fa-solid fa-trash"></i></button>
                    </td>
                </tr>
            `;
        });
    }
}

// --- SALES ---
async function loadSales() {
    STATE.sales = await fetchAPI('/sales');
    renderSalesTable();
}

function renderSalesTable() {
    const tbody = document.querySelector('#sales-table tbody');
    const emptyState = document.getElementById('sales-empty');
    
    tbody.innerHTML = '';
    
    if (STATE.sales.length === 0) {
        emptyState.classList.remove('hidden');
        document.getElementById('sales-table').classList.add('hidden');
    } else {
        emptyState.classList.add('hidden');
        document.getElementById('sales-table').classList.remove('hidden');
        
        STATE.sales.forEach(s => {
            const total = (s.quantity * s.pricePerUnit).toFixed(2);
            tbody.innerHTML += `
                <tr>
                    <td>${s.id}</td>
                    <td><strong>${s.cropName}</strong></td>
                    <td>${s.quantity} kg</td>
                    <td>₹${s.pricePerUnit.toFixed(2)}</td>
                    <td><strong>₹${total}</strong></td>
                    <td>${s.saleDate}</td>
                    <td class="text-right table-actions">
                        <button class="btn-icon-only delete" onclick="confirmDelete('sales', ${s.id}, '${s.cropName.replace(/'/g, "\\'").replace(/"/g, '&quot;')} sale')" title="Delete"><i class="fa-solid fa-trash"></i></button>
                    </td>
                </tr>
            `;
        });
    }
}

// --- STOCK ---
async function loadStock() {
    STATE.stock = await fetchAPI('/stock');
    renderStockTable();
    renderDashStockTable();
}

function getStockStatus(available) {
    if (available > 20) return '<span class="badge badge-success">In Stock</span>';
    if (available > 0) return '<span class="badge badge-warning">Low Stock</span>';
    return '<span class="badge badge-danger">Out of Stock</span>';
}

function renderStockTable() {
    let filteredStock = STATE.stock;
    const search = document.getElementById('search-stock').value.toLowerCase();
    
    if (search) {
        filteredStock = filteredStock.filter(s => s.cropName.toLowerCase().includes(search));
    }

    const tbody = document.querySelector('#stock-page-table tbody');
    const emptyState = document.getElementById('stock-empty');
    
    tbody.innerHTML = '';
    
    if (filteredStock.length === 0) {
        emptyState.classList.remove('hidden');
        document.getElementById('stock-page-table').classList.add('hidden');
    } else {
        emptyState.classList.add('hidden');
        document.getElementById('stock-page-table').classList.remove('hidden');
        
        filteredStock.forEach(s => {
            // Find category from crops list
            const crop = STATE.crops.find(c => c.id === s.cropId);
            const category = crop ? crop.category : '-';
            
            tbody.innerHTML += `
                <tr>
                    <td><strong>${s.cropName}</strong></td>
                    <td>${category}</td>
                    <td>${s.totalHarvested} kg</td>
                    <td>${s.totalSold} kg</td>
                    <td><strong>${s.availableStock} kg</strong></td>
                    <td>${getStockStatus(s.availableStock)}</td>
                </tr>
            `;
        });
    }
}

document.getElementById('search-stock').addEventListener('input', renderStockTable);

// --- DASHBOARD UPDATES ---
function updateDashboardCards() {
    let totalHarvested = 0;
    let totalSold = 0;
    let availableStock = 0;

    STATE.stock.forEach(s => {
        totalHarvested += s.totalHarvested;
        totalSold += s.totalSold;
        availableStock += s.availableStock;
    });

    document.getElementById('dash-total-crops').innerText = STATE.crops.length;
    document.getElementById('dash-total-harvested').innerText = `${totalHarvested} kg`;
    document.getElementById('dash-total-sold').innerText = `${totalSold} kg`;
    document.getElementById('dash-available-stock').innerText = `${availableStock} kg`;
}

function renderDashStockTable() {
    const tbody = document.querySelector('#dash-stock-table tbody');
    const emptyState = document.getElementById('dash-stock-empty');
    
    tbody.innerHTML = '';
    
    if (STATE.stock.length === 0) {
        emptyState.classList.remove('hidden');
        document.getElementById('dash-stock-table').classList.add('hidden');
    } else {
        emptyState.classList.add('hidden');
        document.getElementById('dash-stock-table').classList.remove('hidden');
        
        // Show top 5 items
        const displayStock = STATE.stock.slice(0, 5);
        displayStock.forEach(s => {
            tbody.innerHTML += `
                <tr>
                    <td><strong>${s.cropName}</strong></td>
                    <td>${s.totalHarvested}</td>
                    <td>${s.totalSold}</td>
                    <td>${s.availableStock}</td>
                    <td>${getStockStatus(s.availableStock)}</td>
                </tr>
            `;
        });
    }
}

function updateRecentActivity() {
    const feed = document.getElementById('dash-activity-feed');
    feed.innerHTML = '';

    // Combine harvests and sales
    const activities = [];
    
    STATE.harvests.forEach(h => {
        activities.push({
            type: 'harvest',
            date: h.harvestDate,
            id: h.id, // using id as tie-breaker proxy for recent
            cropName: h.cropName,
            quantity: h.quantity
        });
    });

    STATE.sales.forEach(s => {
        activities.push({
            type: 'sale',
            date: s.saleDate,
            id: s.id,
            cropName: s.cropName,
            quantity: s.quantity
        });
    });

    // Sort by date desc, then ID desc
    activities.sort((a, b) => {
        if (a.date !== b.date) {
            return new Date(b.date) - new Date(a.date);
        }
        return b.id - a.id;
    });

    if (activities.length === 0) {
        feed.innerHTML = '<div class="empty-state">No recent activity yet.</div>';
        return;
    }

    const topActivities = activities.slice(0, 5);
    
    topActivities.forEach(act => {
        const isHarvest = act.type === 'harvest';
        const icon = isHarvest ? '<i class="fa-solid fa-wheat-awn" style="color:var(--success)"></i>' : '<i class="fa-solid fa-cart-shopping" style="color:var(--accent)"></i>';
        const valueClass = isHarvest ? 'positive' : 'negative';
        const sign = isHarvest ? '+' : '-';
        const title = `${act.cropName} ${act.type} recorded`;
        
        // Simple human-readable date logic (just standard string for now)
        const dateStr = act.date; 

        feed.innerHTML += `
            <div class="activity-item">
                <div style="display:flex; gap: 1rem; align-items:center;">
                    ${icon}
                    <div class="act-details">
                        <p>${title}</p>
                        <span>${dateStr}</span>
                    </div>
                </div>
                <div class="act-value ${valueClass}">
                    ${sign}${act.quantity} kg
                </div>
            </div>
        `;
    });
}

function populateDropdowns() {
    const options = STATE.crops.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
    const fallback = '<option value="" disabled selected>[ Select Crop ]</option>';
    
    const harvSelect = document.getElementById('harv-crop');
    const saleSelect = document.getElementById('sale-crop');
    const revSelect = document.getElementById('rev-crop');
    
    // Preserve current selection if exists
    const currHarv = harvSelect.value;
    const currSale = saleSelect.value;
    const currRev = revSelect.value;

    harvSelect.innerHTML = fallback + options;
    saleSelect.innerHTML = fallback + options;
    revSelect.innerHTML = fallback + options;

    if (currHarv) harvSelect.value = currHarv;
    if (currSale) saleSelect.value = currSale;
    if (currRev) revSelect.value = currRev;
}

// --- FORMS & ACTIONS ---

function initForms() {
    // Crop Form
    document.getElementById('form-crop').addEventListener('submit', async (e) => {
        e.preventDefault();
        const btn = document.getElementById('btn-save-crop');
        btn.disabled = true;
        
        const id = document.getElementById('crop-id').value;
        const payload = {
            name: document.getElementById('crop-name').value,
            category: document.getElementById('crop-category').value
        };

        try {
            const method = id ? 'PUT' : 'POST';
            const endpoint = id ? `/crops/${id}` : '/crops';
            
            await fetchAPI(endpoint, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            
            showToast(`Crop ${id ? 'updated' : 'created'} successfully.`);
            closeModal('modal-crop');
            await refreshAllData();
        } catch (err) {
            handleError(err);
        } finally {
            btn.disabled = false;
        }
    });

    // Delete confirm
    document.getElementById('btn-confirm-delete').addEventListener('click', async () => {
        const type = document.getElementById('delete-record-type').value;
        const id = document.getElementById('delete-record-id').value;
        const btn = document.getElementById('btn-confirm-delete');
        btn.disabled = true;

        try {
            const endpoint = type === 'crop' ? `/crops/${id}` : `/${type}/${id}`;
            await fetchAPI(endpoint, { method: 'DELETE' });
            
            let typeName = type.charAt(0).toUpperCase() + type.slice(1);
            if (typeName === 'Sales') typeName = 'Sale';
            if (typeName === 'Harvests') typeName = 'Harvest';
            
            showToast(`${typeName} deleted successfully.`);
            closeModal('modal-delete');
            await refreshAllData();
        } catch (err) {
            closeModal('modal-delete'); // close the confirm modal even on error to show toast clearly
            handleError(err);
        } finally {
            btn.disabled = false;
        }
    });

    // Harvest Form
    document.getElementById('form-harvest').addEventListener('submit', async (e) => {
        e.preventDefault();
        const btn = document.getElementById('btn-save-harvest');
        btn.disabled = true;
        
        const payload = {
            cropId: document.getElementById('harv-crop').value,
            quantity: parseFloat(document.getElementById('harv-qty').value),
            harvestDate: document.getElementById('harv-date').value
        };

        try {
            await fetchAPI('/harvests', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            
            showToast('Harvest recorded successfully.');
            closeModal('modal-harvest');
            await refreshAllData();
        } catch (err) {
            handleError(err);
        } finally {
            btn.disabled = false;
        }
    });

    // Sale Form Logic (Live Estimations)
    const saleQty = document.getElementById('sale-qty');
    const salePrice = document.getElementById('sale-price');
    const saleCrop = document.getElementById('sale-crop');
    
    function updateSaleEstimate() {
        const q = parseFloat(saleQty.value) || 0;
        const p = parseFloat(salePrice.value) || 0;
        document.getElementById('sale-estimate').innerText = `₹${(q * p).toFixed(2)}`;
    }
    
    saleQty.addEventListener('input', updateSaleEstimate);
    salePrice.addEventListener('input', updateSaleEstimate);

    saleCrop.addEventListener('change', () => {
        const cid = parseInt(saleCrop.value);
        const st = STATE.stock.find(s => s.cropId === cid);
        const av = st ? st.availableStock : 0;
        document.getElementById('sale-stock-hint').innerText = `Available Stock: ${av} kg`;
    });

    document.getElementById('form-sale').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        // Frontend warning check (optional as per prompt, but submit anyway to let backend reject)
        const cid = parseInt(saleCrop.value);
        const q = parseFloat(saleQty.value);
        const st = STATE.stock.find(s => s.cropId === cid);
        const av = st ? st.availableStock : 0;
        
        if (q > av) {
            // We just warn in console, or we could use confirm(). Let's just pass it to backend for authoritative rejection as requested.
        }

        const btn = document.getElementById('btn-save-sale');
        btn.disabled = true;
        
        const payload = {
            cropId: cid,
            quantity: q,
            pricePerUnit: parseFloat(salePrice.value),
            saleDate: document.getElementById('sale-date').value
        };

        try {
            await fetchAPI('/sales', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            
            showToast('Sale recorded successfully.');
            closeModal('modal-sale');
            await refreshAllData();
        } catch (err) {
            handleError(err);
        } finally {
            btn.disabled = false;
        }
    });

    // Revenue Form
    document.getElementById('form-revenue').addEventListener('submit', async (e) => {
        e.preventDefault();
        const btn = document.getElementById('btn-generate-rev');
        btn.disabled = true;
        
        const cid = document.getElementById('rev-crop').value;
        const from = document.getElementById('rev-from').value;
        const to = document.getElementById('rev-to').value;

        try {
            const data = await fetchAPI(`/reports/revenue?cropId=${cid}&from=${from}&to=${to}`);
            
            document.getElementById('revenue-result-container').classList.remove('hidden');
            document.getElementById('rev-total-amount').innerText = `₹${data.totalRevenue.toFixed(2)}`;
            
            document.getElementById('rev-table-crop').innerText = data.cropName;
            document.getElementById('rev-table-dates').innerText = `${data.fromDate} to ${data.toDate}`;
            document.getElementById('rev-table-revenue').innerHTML = `<strong>₹${data.totalRevenue.toFixed(2)}</strong>`;
            
            showToast('Report generated successfully.');
        } catch (err) {
            handleError(err);
        } finally {
            btn.disabled = false;
        }
    });
}

// Global functions for inline onclick handlers
window.editCrop = function(id) {
    const crop = STATE.crops.find(c => c.id === id);
    if (!crop) return;
    
    document.getElementById('crop-id').value = crop.id;
    document.getElementById('crop-name').value = crop.name;
    document.getElementById('crop-category').value = crop.category;
    document.getElementById('crop-modal-title').innerText = 'Edit Crop';
    
    openModal('modal-crop');
};

window.confirmDelete = function(type, id, name) {
    document.getElementById('delete-record-type').value = type;
    document.getElementById('delete-record-id').value = id;
    let typeName = type.charAt(0).toUpperCase() + type.slice(1);
    if (type === 'harvestBatch') typeName = 'Harvest';
    
    document.getElementById('delete-modal-title').innerText = `Delete ${typeName}?`;
    document.getElementById('delete-confirm-text').innerHTML = `Are you sure you want to delete this ${typeName.toLowerCase()}: <strong>${name}</strong>?`;
    openModal('modal-delete');
};

// Reset crop form title on regular open
const originalOpenModal = window.openModal;
window.openModal = function(id) {
    if (id === 'modal-crop') {
        document.getElementById('crop-modal-title').innerText = 'Add New Crop';
    }
    originalOpenModal(id);
};
