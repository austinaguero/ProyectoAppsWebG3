// Carga los datos del cliente en el modal Agregar o Editar (HU-01 y HU-03)
document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('clienteModal');
    if (!modal) {
        return;
    }
    const form = document.getElementById('clienteForm');
    const titulo = document.getElementById('clienteModalTitulo');
    const editar = document.getElementById('clienteEditar');
    const cedula = document.getElementById('clienteCedula');
    const nombre = document.getElementById('clienteNombre');
    const apellido1 = document.getElementById('clienteApellido1');
    const apellido2 = document.getElementById('clienteApellido2');
    const referencia = document.getElementById('clienteReferencia');

    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        if (!button) {
            return;
        }
        const modo = button.getAttribute('data-modo');
        // Se limpia cualquier información de una apertura anterior
        form.reset();
        editar.value = '';
        cedula.readOnly = false;
        titulo.textContent = button.getAttribute('data-titulo') || '';
        if (modo === 'editar') {
            editar.value = 'true';
            // La cédula es la llave del cliente, no se permite cambiarla
            cedula.value = button.getAttribute('data-cedula') || '';
            cedula.readOnly = true;
            nombre.value = button.getAttribute('data-nombre') || '';
            apellido1.value = button.getAttribute('data-apellido1') || '';
            apellido2.value = button.getAttribute('data-apellido2') || '';
            referencia.value = button.getAttribute('data-referencia') || '';
        }
    });
});
