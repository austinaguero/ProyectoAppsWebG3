//Para insertar información en el modal de eliminar según el registro...
document.addEventListener('DOMContentLoaded', function () {
    const confirmModal = document.getElementById('confirmModal');
    if (!confirmModal) {
        return;
    }
    confirmModal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        document.getElementById('modalId').value = button.getAttribute('data-bs-id');
        document.getElementById('modalDescripcion').textContent = button.getAttribute('data-bs-descripcion');
    });
});

//Pide confirmación antes de guardar los cambios de un registro existente.
//El form indica en data-confirmar-edicion el id del campo oculto con la llave:
//si el campo está vacío es un registro nuevo y se guarda sin preguntar.
document.addEventListener('DOMContentLoaded', function () {
    const confirmEdicion = document.getElementById('confirmEdicionModal');
    if (!confirmEdicion) {
        return;
    }
    let formPendiente = null;
    let confirmado = false;

    document.querySelectorAll('form[data-confirmar-edicion]').forEach(function (form) {
        form.addEventListener('submit', function (event) {
            const id = document.getElementById(form.getAttribute('data-confirmar-edicion'));
            if (!id || !id.value) {
                return;
            }
            event.preventDefault();
            formPendiente = form;
            confirmado = false;
            // Se cierra el formulario y, cuando termina de cerrarse, se abre la confirmación
            const modalForm = form.closest('.modal');
            modalForm.addEventListener('hidden.bs.modal', function () {
                bootstrap.Modal.getOrCreateInstance(confirmEdicion).show();
            }, {once: true});
            bootstrap.Modal.getOrCreateInstance(modalForm).hide();
        });
    });

    document.getElementById('confirmEdicionBoton').addEventListener('click', function () {
        if (formPendiente) {
            confirmado = true;
            formPendiente.submit();
        }
    });

    // Si no confirma, se vuelve a mostrar el formulario con los datos digitados
    confirmEdicion.addEventListener('hidden.bs.modal', function () {
        if (formPendiente && !confirmado) {
            bootstrap.Modal.getOrCreateInstance(formPendiente.closest('.modal')).show();
            formPendiente = null;
        }
    });
});

//Para quitar toast después de unos segundos
document.addEventListener('DOMContentLoaded', function () {
    setTimeout(() => {
        document.querySelectorAll('.toast').forEach(t => t.classList.remove('show'));
    }, 4000);
});
