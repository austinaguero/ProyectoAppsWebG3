// Carga los datos del servicio en el modal Agregar o Editar
document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('servicioModal');
    if (!modal) {
        return;
    }
    const form = document.getElementById('servicioForm');
    const titulo = document.getElementById('servicioModalTitulo');
    const id = document.getElementById('servicioId');
    const fallecido = document.getElementById('servicioFallecido');
    const tipo = document.getElementById('servicioTipo');
    const descripcion = document.getElementById('servicioDescripcion');
    const fecha = document.getElementById('servicioFecha');
    const fechaHoy = fecha.value;

    // Agrega al select un fallecido que ya no está activo, para poder editar su servicio
    function asegurarOpcion(select, valor, texto) {
        if (valor && !select.querySelector('option[value="' + valor + '"]')) {
            select.add(new Option(texto, valor));
        }
    }

    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        if (!button) {
            return;
        }
        const modo = button.getAttribute('data-modo');
        // Se limpia cualquier información de una apertura anterior
        form.reset();
        id.value = '';
        fecha.value = fechaHoy;
        titulo.textContent = button.getAttribute('data-titulo') || '';
        if (modo === 'editar') {
            id.value = button.getAttribute('data-id') || '';
            asegurarOpcion(fallecido, button.getAttribute('data-fallecido'), button.getAttribute('data-fallecido-nombre'));
            fallecido.value = button.getAttribute('data-fallecido') || '';
            tipo.value = button.getAttribute('data-tipo') || '';
            descripcion.value = button.getAttribute('data-descripcion') || '';
            fecha.value = button.getAttribute('data-fecha') || '';
        }
    });
});
