/*!
* Start Bootstrap - SB Admin v7.0.7 (https://startbootstrap.com/template/sb-admin)
* Copyright 2013-2023 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-sb-admin/blob/master/LICENSE)
*/

// Scripts principales
window.addEventListener('DOMContentLoaded', event => {
    // Toggle de navegación lateral
    const sidebarToggle = document.body.querySelector('#sidebarToggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', event => {
            event.preventDefault();
            document.body.classList.toggle('sb-sidenav-toggled');
            localStorage.setItem('sb|sidebar-toggle', document.body.classList.contains('sb-sidenav-toggled'));
        });
    }

    // Resaltar filas de tabla al pasar el mouse
    const tableRows = document.querySelectorAll('tbody tr');
    tableRows.forEach(row => {
        row.addEventListener('mouseenter', function () {
            this.classList.add('table-active');
        });
        row.addEventListener('mouseleave', function () {
            this.classList.remove('table-active');
        });
    });

    // Configuración de tooltips para botones de acción
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

// Validación del formulario de edición
(function () {
    'use strict';

    const form = document.querySelector('.needs-validation');
    if (form) {
        const phoneInput = document.querySelector('#phone-input');
        const phoneRegex = /^\d{4}-\d{4}$/;

        form.addEventListener('submit', function (event) {
            if (!form.checkValidity() || (phoneInput && !phoneRegex.test(phoneInput.value))) {
                event.preventDefault();
                event.stopPropagation();

                const invalidFields = form.querySelectorAll(':invalid');
                invalidFields.forEach(field => {
                    field.classList.add('is-invalid');
                    if (invalidFields[0] === field) {
                        field.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    }
                });

                Swal.fire({
                    icon: 'error',
                    title: 'Error de validación',
                    text: 'Por favor complete todos los campos requeridos correctamente',
                    confirmButtonColor: '#3a7bd5'
                });
            }
        }, false);

        form.querySelectorAll('input, select, textarea').forEach(input => {
            input.addEventListener('input', () => {
                if (input.checkValidity()) {
                    input.classList.remove('is-invalid');
                } else {
                    input.classList.add('is-invalid');
                }
            });

            input.addEventListener('blur', () => {
                if (!input.checkValidity()) {
                    input.classList.add('is-invalid');
                }
            });
        });
    }
})();

// Función para confirmar eliminación
function confirmDelete(event, url) {
    event.preventDefault();
    Swal.fire({
        title: '¿Estás seguro?',
        text: "¡Esta acción eliminará el equipo y asignacion!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3a7bd5',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href = url;
        }
    });
}

// Inicialización de elementos cuando se carga contenido dinámico
document.addEventListener('DOMContentLoaded', function () {
    // Configuración de mensajes flash
    const flashMessage = document.getElementById('flash-message');
    if (flashMessage) {
        const messageType = flashMessage.getAttribute('data-type');
        const messageText = flashMessage.getAttribute('data-message');

        if (messageType && messageText) {
            Swal.fire({
                icon: messageType,
                title: messageType === 'success' ? 'Éxito' : 'Error',
                text: messageText,
                confirmButtonColor: '#3a7bd5'
            });
        }
    }
});


//calendarioVista
document.getElementById("calendarioForm").addEventListener("submit", function (event) {
    const fechaInicioInput = document.getElementById("fechaInicio").value;
    const fechaFinInput = document.getElementById("fechaFin").value;
    const errorDiv = document.getElementById("fechaError");

    // Limpiar errores anteriores
    errorDiv.textContent = "";

    if (fechaInicioInput && fechaFinInput) {
        const fechaInicio = new Date(fechaInicioInput);
        const fechaFin = new Date(fechaFinInput);

        // Fecha esperada = fechaInicio + 1 mes
        const fechaEsperada = new Date(fechaInicio);
        fechaEsperada.setMonth(fechaEsperada.getMonth() + 1);

        // Comparar solo fecha (día/mes/año)
        const mismoDia = fechaFin.getDate() === fechaEsperada.getDate();
        const mismoMes = fechaFin.getMonth() === fechaEsperada.getMonth();
        const mismoAnio = fechaFin.getFullYear() === fechaEsperada.getFullYear();

        if (!(mismoDia && mismoMes && mismoAnio)) {
            event.preventDefault(); // detener envío
            errorDiv.textContent = "La fecha de fin debe ser exactamente un mes después de la fecha de inicio.";
        }
    }
});