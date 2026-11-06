// Prepara el modal para registrar un contrato nuevo
document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('financiamientoModal');
    if (!modal) {
        return;
    }
    const form = document.getElementById('financiamientoForm');
    const titulo = document.getElementById('financiamientoModalTitulo');

    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        if (!button) {
            return;
        }
        // Se limpia cualquier información de una apertura anterior
        form.reset();
        titulo.textContent = button.getAttribute('data-titulo') || '';
    });
});
