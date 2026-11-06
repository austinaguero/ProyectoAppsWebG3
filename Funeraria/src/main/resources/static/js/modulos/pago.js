// Carga los datos del contrato en el modal Registrar pago (HU-12).
// Se pueden pagar varias cuotas seguidas: el total se calcula con las cuotas pendientes
// del detalle del contrato (cada fila trae su monto y su recargo si está vencida).
document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('pagoModal');
    if (!modal) {
        return;
    }
    const form = document.getElementById('pagoForm');
    const titulo = document.getElementById('pagoModalTitulo');
    const cuota = document.getElementById('pagoCuota');
    const cliente = document.getElementById('pagoCliente');
    const concepto = document.getElementById('pagoConcepto');
    const cantidad = document.getElementById('pagoCantidad');
    const cuotaMonto = document.getElementById('pagoCuotaMonto');
    const recargo = document.getElementById('pagoRecargo');
    const monto = document.getElementById('pagoMonto');
    let pendientes = [];
    let conceptoUna = '';
    let conceptoVarias = '';

    // Los montos se suman en céntimos para no perder decimales
    function centimos(valor) {
        return Math.round(parseFloat(valor || '0') * 100);
    }

    function formato(cents) {
        return (cents / 100).toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2});
    }

    // Recalcula el concepto y los montos según la cantidad de cuotas escogida
    function calcular() {
        const n = parseInt(cantidad.value || '1', 10);
        let sumaCuotas = 0;
        let sumaRecargos = 0;
        for (let i = 0; i < n && i < pendientes.length; i++) {
            sumaCuotas += centimos(pendientes[i].getAttribute('data-monto'));
            sumaRecargos += centimos(pendientes[i].getAttribute('data-recargo'));
        }
        if (n > 1) {
            concepto.value = conceptoVarias
                    .replace('{desde}', pendientes[0].getAttribute('data-numero'))
                    .replace('{hasta}', pendientes[n - 1].getAttribute('data-numero'));
        } else {
            concepto.value = conceptoUna;
        }
        cuotaMonto.value = '₡' + formato(sumaCuotas);
        recargo.value = '₡' + formato(sumaRecargos);
        // Se propone el monto exacto: las cuotas más el recargo de las vencidas
        monto.value = ((sumaCuotas + sumaRecargos) / 100).toFixed(2);
    }

    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        if (!button) {
            return;
        }
        // Se limpia cualquier información de una apertura anterior
        form.reset();
        titulo.textContent = button.getAttribute('data-titulo') || '';
        cuota.value = button.getAttribute('data-cuota') || '';
        cliente.value = button.getAttribute('data-cliente') || '';
        conceptoUna = button.getAttribute('data-concepto') || '';
        conceptoVarias = button.getAttribute('data-concepto-varias') || '';
        // Cuotas sin pagar del contrato, en orden de número
        pendientes = Array.from(document.querySelectorAll(
                '#cuotas' + button.getAttribute('data-financiamiento') + ' tr[data-pendiente="true"]'));
        cantidad.innerHTML = '';
        for (let i = 1; i <= Math.max(pendientes.length, 1); i++) {
            cantidad.add(new Option(String(i), String(i)));
        }
        cantidad.value = '1';
        calcular();
    });

    cantidad.addEventListener('change', calcular);
});
