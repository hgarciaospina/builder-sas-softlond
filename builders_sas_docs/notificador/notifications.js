/* ============================================================
   CONFIGURACIÓN DEL USUARIO
   ============================================================ */

const USER_ID = 2; // Cambiar manualmente si es necesario
const URL = `http://localhost:9090/notifications/by-user?userId=${USER_ID}`;

const openBtn = document.getElementById("openBtn");
const closeBtn = document.getElementById("closeBtn");
const overlay = document.getElementById("overlay");
const modal = document.getElementById("modal");
const list = document.getElementById("list");
const toastContainer = document.getElementById("toastContainer");
const badge = document.getElementById("badge");
const clearBtn = document.getElementById("clearBtn");

let lastCount = 0;
let readSet = new Set();

/* ============================================================
   ABRIR MODAL
   ============================================================ */
openBtn.onclick = () => {
  overlay.classList.remove("hidden");
  modal.classList.remove("hidden");

  fetch(URL)
    .then((r) => r.json())
    .then((data) => {
      renderList(data);
      updateBadge(data);
    });
};

/* ============================================================
   CERRAR MODAL
   ============================================================ */
closeBtn.onclick = closeModal;
overlay.onclick = (e) => {
  if (e.target === overlay) closeModal();
};

function closeModal() {
  overlay.classList.add("hidden");
  modal.classList.add("hidden");
}

/* ============================================================
   FORMATEO PROFESIONAL DE NOTIFICACIONES
   ============================================================ */

function formatNotification(n) {
  let p = n.payload;

  /* ========================================================
     ORDER_CREATED
     Formato a líneas separadas
     ======================================================== */
  if (n.eventType === "ORDER_CREATED") {
    const match = p.match(
      /Solicitud (\d+).*?Orden(\d+).*?Inicio=(\d{4}-\d{2}-\d{2}).*?Fin=(\d{4}-\d{2}-\d{2}).*?\(([-\d.]+),([-\d.]+)\)/
    );

    if (match) {
      const [_, solicitud, orden, inicio, fin, lat, lng] = match;
      return `
        ✔ <strong>ORDEN CREADA</strong><br>
        Solicitud: <strong>${solicitud}</strong><br>
        Número de Orden: <strong>${orden}</strong><br>
        Fecha de Inicio: <strong>${inicio}</strong><br>
        Fecha de Terminación: <strong>${fin}</strong><br>
        Coordenadas: <strong>(${lat}, ${lng})</strong>
      `;
    }
  }

  /* ========================================================
     CONSTRUCTION_REQUEST_CREATED
     ======================================================== */
  if (n.eventType === "CONSTRUCTION_REQUEST_CREATED") {
    const match = p.match(
      /Solicitud (\d+) creada.*?Proyecto=(.*?), Tipo=(.*?), Coordenadas=\(([-\d.]+),([-\d.]+)\)/
    );
    if (match) {
      const [_, id, proyecto, tipo, lat, lng] = match;

      return `
        ✔ <strong>SOLICITUD CREADA</strong><br>
        ID Solicitud: <strong>${id}</strong><br>
        Proyecto: <strong>${proyecto}</strong><br>
        Tipo de Construcción: <strong>${tipo}</strong><br>
        Coordenadas: <strong>(${lat}, ${lng})</strong>
      `;
    }
  }

  /* ========================================================
     CONSTRUCTION_REQUEST_REJECTED
     ======================================================== */
  if (n.eventType === "CONSTRUCTION_REQUEST_REJECTED") {
    return `
      ❌ <strong>SOLICITUD RECHAZADA</strong><br>
      ${p.replaceAll("\n", "<br>")}
    `;
  }

  /* ========================================================
     CONSTRUCTION_REQUEST_APPROVED
     ======================================================== */
  if (n.eventType === "CONSTRUCTION_REQUEST_APPROVED") {
    return `
      ✔ <strong>SOLICITUD APROBADA</strong><br>
      ${p.replaceAll("\n", "<br>")}
    `;
  }

  /* ========================================================
     CONSTRUCTION_REQUEST_FAILED
     ======================================================== */
  if (n.eventType === "CONSTRUCTION_REQUEST_FAILED") {
    return `
      ⚠️ <strong>ERROR EN SOLICITUD</strong><br>
      ${p.replaceAll("\n", "<br>")}
    `;
  }

  /* ========================================================
     Fallback (si algo no hace match)
     ======================================================== */
  return p.replaceAll("\n", "<br>");
}

/* ============================================================
   RENDERIZAR LISTA
   ============================================================ */
function renderList(data) {
  list.innerHTML = "";

  if (!data.length) {
    list.innerHTML =
      "<p style='text-align:center;color:#64748b;'>No hay notificaciones</p>";
    return;
  }

  data.forEach((n) => {
    const div = document.createElement("div");
    div.className = "notif";
    div.innerHTML = `
      <strong>${n.eventType}</strong>
      <small>${new Date(n.timestamp).toLocaleString()}</small>
      <p>${formatNotification(n)}</p>
    `;
    list.appendChild(div);
  });
}

/* ============================================================
   TOAST VISUAL
   ============================================================ */
function showToast(n) {
  const toast = document.createElement("div");
  toast.className = "toast";
  toast.innerHTML = `${formatNotification(n)}`;
  toastContainer.appendChild(toast);
  setTimeout(() => toast.remove(), 3500);
}

/* ============================================================
   POLLING CADA 2s
   ============================================================ */
setInterval(() => {
  fetch(URL)
    .then((r) => r.json())
    .then((data) => {
      if (data.length > lastCount) {
        const newOnes = data.slice(lastCount);
        newOnes.forEach((n) => showToast(n));
        updateBadge(data);
      }
      lastCount = data.length;
    });
}, 2000);

/* ============================================================
   BADGE (no leídas)
   ============================================================ */
function updateBadge(data) {
  const unread = data.length - readSet.size;
  if (unread > 0) {
    badge.textContent = unread;
    badge.classList.remove("hidden");
  } else {
    badge.classList.add("hidden");
  }
}

/* ============================================================
   MARCAR COMO LEÍDAS (solo frontend)
   ============================================================ */
clearBtn.onclick = () => {
  fetch(URL)
    .then((r) => r.json())
    .then((data) => {
      data.forEach((n) => readSet.add(n.eventType + n.timestamp));
      updateBadge(data);
      renderList(data);
    });
};
