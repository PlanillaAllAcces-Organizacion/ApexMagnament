/*!
    * Start Bootstrap - SB Admin v7.0.7 (https://startbootstrap.com/template/sb-admin)
    * Copyright 2013-2023 Start Bootstrap
    * Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-sb-admin/blob/master/LICENSE)
    */
// 
// Scripts
// 

window.addEventListener('DOMContentLoaded', event => {

    // Toggle the side navigation
    const sidebarToggle = document.body.querySelector('#sidebarToggle');
    if (sidebarToggle) {
        // Uncomment Below to persist sidebar toggle between refreshes
        // if (localStorage.getItem('sb|sidebar-toggle') === 'true') {
        //     document.body.classList.toggle('sb-sidenav-toggled');
        // }
        sidebarToggle.addEventListener('click', event => {
            event.preventDefault();
            document.body.classList.toggle('sb-sidenav-toggled');
            localStorage.setItem('sb|sidebar-toggle', document.body.classList.contains('sb-sidenav-toggled'));
        });
    }

});


//SCRIPT PARA LA VISTA EDIT EQUIPO

// Validación del formulario
(function () {
    'use strict';

    const form = document.querySelector('.needs-validation');

    // Validar al enviar
    form.addEventListener('submit', function (event) {
        if (!form.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();

            // Mostrar todos los errores
            const invalidFields = form.querySelectorAll(':invalid');
            invalidFields.forEach(field => {
                field.classList.add('is-invalid');

                // Desplazarse al primer campo inválido
                if (invalidFields[0] === field) {
                    field.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
            });

            // Mostrar mensaje con SweetAlert
            Swal.fire({
                icon: 'error',
                title: 'Error de validación',
                text: 'Por favor complete todos los campos requeridos correctamente',
                confirmButtonColor: '#3a7bd5',
                scrollbarPadding: false
            });
        }

        form.classList.add('was-validated');
    }, false);

    // Validación en tiempo real
    form.querySelectorAll('input, select, textarea').forEach(input => {
        input.addEventListener('input', () => {
            if (input.checkValidity()) {
                input.classList.remove('is-invalid');
            } else {
                input.classList.add('is-invalid');
            }
        });

        // Validar al perder foco
        input.addEventListener('blur', () => {
            if (!input.checkValidity()) {
                input.classList.add('is-invalid');
            }
        });
    });
})();

// Vista previa de imagen con SweetAlert para errores
function previewImage(input) {
    const currentImg = document.getElementById('currentImg');
    const preview = document.getElementById('imgPreview');
    const fileInfo = document.getElementById('fileInfo');
    const fileName = document.getElementById('fileName');
    const fileSize = document.getElementById('fileSize');
    const displayFileName = document.getElementById('displayFileName');
    const selectedFileNameDiv = document.getElementById('selectedFileName');
    const maxSize = 2 * 1024 * 1024; // 2MB

    if (input.files && input.files[0]) {
        const file = input.files[0];

        // Validar tamaño
        if (file.size > maxSize) {
            Swal.fire({
                icon: 'error',
                title: 'Archivo demasiado grande',
                text: 'La imagen no debe exceder 2MB',
                confirmButtonColor: '#3a7bd5'
            });
            input.value = '';
            resetImagePreview();
            return;
        }

        // Validar tipo
        if (!file.type.match('image/jpeg') && !file.type.match('image/png')) {
            Swal.fire({
                icon: 'error',
                title: 'Formato no soportado',
                text: 'Solo se permiten imágenes JPEG o PNG',
                confirmButtonColor: '#3a7bd5'
            });
            input.value = '';
            resetImagePreview();
            return;
        }

        // Mostrar información del archivo
        fileName.textContent = file.name.length > 20 ?
            file.name.substring(0, 20) + '...' : file.name;
        fileSize.textContent = (file.size / 1024).toFixed(2);
        fileInfo.style.display = 'block';

        // Mostrar nombre completo del archivo
        displayFileName.textContent = file.name;
        selectedFileNameDiv.style.display = 'block';

        // Mostrar vista previa
        const reader = new FileReader();
        reader.onload = function (e) {
            preview.src = e.target.result;
            preview.style.display = 'block';
            currentImg.style.opacity = '0.5';
        }
        reader.readAsDataURL(file);
    } else {
        resetImagePreview();
    }
}

function resetImagePreview() {
    const currentImg = document.getElementById('currentImg');
    const preview = document.getElementById('imgPreview');
    const fileInfo = document.getElementById('fileInfo');
    const selectedFileNameDiv = document.getElementById('selectedFileName');

    preview.style.display = 'none';
    currentImg.style.opacity = '1';
    fileInfo.style.display = 'none';
    selectedFileNameDiv.style.display = 'none';
}

// Función para confirmar eliminación con SweetAlert
function confirmDelete(event, url) {
    event.preventDefault();
    Swal.fire({
        title: '¿Estás seguro?',
        text: "¡No podrás revertir esta acción!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3a7bd5',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar',
        scrollbarPadding: false
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href = url;
        }
    });
}

class FlashMessages {
    constructor() {
        this.init();
    }

    init() {
        document.addEventListener('DOMContentLoaded', () => {
            this.showSwalMessage();
            this.showSuccessMessage();
            this.showErrorMessage();
            this.showInfoMessage();
            this.showWarningMessage();
        });
    }

    showSwalMessage() {
        const swalMessage = this.getFlashMessage('swal');
        if (swalMessage) {
            Swal.fire({
                title: swalMessage.title,
                text: swalMessage.text,
                icon: swalMessage.icon,
                confirmButtonColor: '#3a7bd5',
                timer: 3000,
                timerProgressBar: true,
                scrollbarPadding: false
            });
        }
    }

    showSuccessMessage() {
        const message = this.getFlashMessage('success');
        if (message) {
            Swal.fire({
                icon: 'success',
                title: '¡Éxito!',
                text: message,
                confirmButtonColor: '#3a7bd5',
                timer: 3000,
                timerProgressBar: true,
                didClose: () => {

                }
            });
        }
    }

    showErrorMessage() {
        const message = this.getFlashMessage('error');
        if (message) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: message,
                confirmButtonColor: '#3a7bd5'
            });
        }
    }

    showInfoMessage() {
        const message = this.getFlashMessage('info');
        if (message) {
            Swal.fire({
                icon: 'info',
                title: 'Información',
                text: message,
                confirmButtonColor: '#3a7bd5'
            });
        }
    }

    showWarningMessage() {
        const message = this.getFlashMessage('warning');
        if (message) {
            Swal.fire({
                icon: 'warning',
                title: 'Advertencia',
                text: message,
                confirmButtonColor: '#3a7bd5'
            });
        }
    }

    getFlashMessage(type) {
        // Usando Thymeleaf inline javascript para obtener el mensaje
        return /*[[${type}]]*/ null;
    }

    redirectIfNeeded() {
        // Implementar lógica de redirección si es necesaria
        // Ejemplo: window.location.href = /*[[@{'/ruta'}}]]*/;
    }
}

// Inicializar automáticamente cuando se carga el script
new FlashMessages();
