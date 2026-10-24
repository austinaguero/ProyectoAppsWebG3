// Carga los datos del sepelio en el modal Programar o Editar
document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('sepelioModal');
    if (!modal) {
        return;
    }
    const form = document.getElementById('sepelioForm');
    const titulo = document.getElementById('sepelioModalTitulo');
    const id = document.getElementById('sepelioId');
    const servicio = document.getElementById('sepelioServicio');
    const servicioFijo = document.getElementById('sepelioServicioFijo');
    const servicioTexto = document.getElementById('sepelioServicioTexto');
    const grupoServicio = document.getElementById('sepelioGrupoServicio');
    const grupoServicioTexto = document.getElementById('sepelioGrupoServicioTexto');
    const fecha = document.getElementById('sepelioFecha');
    const hora = document.getElementById('sepelioHora');
    const responsable = document.getElementById('sepelioResponsable');
    const unidad = document.getElementById('sepelioUnidad');
    const sinUnidades = document.getElementById('sepelioSinUnidades');
    const guardar = document.getElementById('sepelioGuardar');
    // Cantidad de unidades disponibles que vienen del servidor (sin contar "Seleccione...")
    const unidadesDisponibles = unidad.options.length - 1;

    // Al editar el servicio no cambia: se muestra como texto y se envía en un campo oculto
    function modoServicio(editar) {
        grupoServicio.classList.toggle('d-none', editar);
        grupoServicioTexto.classList.toggle('d-none', !editar);
        servicio.disabled = editar;
        servicioFijo.disabled = !editar;
    }

    // La unidad actual del sepelio ya está reservada: se agrega al select para poder conservarla
    function prepararUnidadActual(valor, texto) {
        unidad.querySelectorAll('option[data-actual]').forEach(o => o.remove());
        if (valor) {
            const opcion = new Option(texto, valor);
            opcion.setAttribute('data-actual', 'true');
            unidad.add(opcion, 1);
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
        servicioFijo.value = '';
        servicioTexto.value = '';
        titulo.textContent = button.getAttribute('data-titulo') || '';
        if (modo === 'editar') {
            modoServicio(true);
            id.value = button.getAttribute('data-id') || '';
            servicioFijo.value = button.getAttribute('data-servicio') || '';
            servicioTexto.value = button.getAttribute('data-servicio-texto') || '';
            fecha.value = button.getAttribute('data-fecha') || '';
            hora.value = button.getAttribute('data-hora') || '';
            responsable.value = button.getAttribute('data-responsable') || '';
            prepararUnidadActual(button.getAttribute('data-unidad'), button.getAttribute('data-unidad-texto'));
            unidad.value = button.getAttribute('data-unidad') || '';
            // Al editar se puede conservar la unidad actual aunque no haya otras libres
            guardar.disabled = unidad.options.length <= 1;
        } else {
            modoServicio(false);
            prepararUnidadActual(null, null);
            // Sin espacio disponible no se puede programar un sepelio nuevo
            guardar.disabled = unidadesDisponibles === 0;
        }
        if (sinUnidades) {
            sinUnidades.classList.toggle('d-none', modo === 'editar' && unidad.options.length > 1);
        }
    });
});
