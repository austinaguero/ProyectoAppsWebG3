// Carga los datos de la unidad de sepultura en el modal Agregar o Editar
document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('unidadModal');
    if (!modal) {
        return;
    }
    const form = document.getElementById('unidadForm');
    const titulo = document.getElementById('unidadModalTitulo');
    const id = document.getElementById('unidadId');
    const cementerio = document.getElementById('unidadCementerio');
    const sector = document.getElementById('unidadSector');
    const bloque = document.getElementById('unidadBloque');
    const numero = document.getElementById('unidadNumero');
    const capacidad = document.getElementById('unidadCapacidad');
    const tipo = document.getElementById('unidadTipo');
    const estado = document.getElementById('unidadEstado');

    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        if (!button) {
            return;
        }
        const modo = button.getAttribute('data-modo');
        // Se limpia cualquier información de una apertura anterior
        form.reset();
        id.value = '';
        titulo.textContent = button.getAttribute('data-titulo') || '';
        if (modo === 'editar') {
            id.value = button.getAttribute('data-id') || '';
            cementerio.value = button.getAttribute('data-cementerio') || '';
            sector.value = button.getAttribute('data-sector') || '';
            bloque.value = button.getAttribute('data-bloque') || '';
            numero.value = button.getAttribute('data-numero') || '';
            capacidad.value = button.getAttribute('data-capacidad') || '';
            tipo.value = button.getAttribute('data-tipo') || '';
            estado.value = button.getAttribute('data-estado') || '';
        } else {
            // Una unidad nueva inicia disponible (primera opción del select)
            estado.selectedIndex = 0;
        }
    });
});
