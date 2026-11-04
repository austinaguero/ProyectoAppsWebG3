// Carga los datos del fallecido en el modal Registrar o Editar
document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('fallecidoModal');
    if (!modal) {
        return;
    }
    const form = document.getElementById('fallecidoForm');
    const titulo = document.getElementById('fallecidoModalTitulo');
    const edicion = document.getElementById('fallecidoEdicion');
    const cedula = document.getElementById('fallecidoCedula');
    const cliente = document.getElementById('fallecidoCliente');
    const nombre = document.getElementById('fallecidoNombre');
    const apellido1 = document.getElementById('fallecidoApellido1');
    const apellido2 = document.getElementById('fallecidoApellido2');
    const nacimiento = document.getElementById('fallecidoNacimiento');
    const fallecimiento = document.getElementById('fallecidoFallecimiento');
    const lugar = document.getElementById('fallecidoLugar');
    const causa = document.getElementById('fallecidoCausa');
    const provincia = document.getElementById('fallecidoProvincia');
    const canton = document.getElementById('fallecidoCanton');
    const distrito = document.getElementById('fallecidoDistrito');
    const sennas = document.getElementById('fallecidoSennas');

    // Muestra solo las opciones que pertenecen al valor escogido en el select padre
    // (cantones de la provincia, distritos del cantón) y limpia la selección anterior
    function filtrar(select, atributo, valorPadre) {
        select.value = '';
        Array.from(select.options).forEach(function (o) {
            if (o.value === '') {
                return;
            }
            const visible = valorPadre !== '' && o.getAttribute(atributo) === valorPadre;
            o.hidden = !visible;
            o.disabled = !visible;
        });
    }

    provincia.addEventListener('change', function () {
        filtrar(canton, 'data-provincia', provincia.value);
        filtrar(distrito, 'data-canton', '');
    });
    canton.addEventListener('change', function () {
        filtrar(distrito, 'data-canton', canton.value);
    });

    // Carga una dirección completa respetando el orden provincia, cantón y distrito
    function cargarDireccion(idProvincia, idCanton, idDistrito) {
        provincia.value = idProvincia || '';
        filtrar(canton, 'data-provincia', provincia.value);
        canton.value = idCanton || '';
        filtrar(distrito, 'data-canton', canton.value);
        distrito.value = idDistrito || '';
    }

    // Ninguna de las dos fechas puede ser futura
    const hoy = new Date().toISOString().substring(0, 10);
    nacimiento.max = hoy;
    fallecimiento.max = hoy;

    // Agrega al select un cliente que ya no está activo, para poder editar al fallecido
    function asegurarOpcion(select, valor) {
        if (valor && !select.querySelector('option[value="' + valor + '"]')) {
            select.add(new Option(valor, valor));
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
        edicion.value = '';
        cedula.readOnly = false;
        titulo.textContent = button.getAttribute('data-titulo') || '';
        if (modo === 'editar') {
            // La cédula es la llave: al editar no se puede cambiar
            edicion.value = 'true';
            cedula.value = button.getAttribute('data-cedula') || '';
            cedula.readOnly = true;
            asegurarOpcion(cliente, button.getAttribute('data-cliente'));
            cliente.value = button.getAttribute('data-cliente') || '';
            nombre.value = button.getAttribute('data-nombre') || '';
            apellido1.value = button.getAttribute('data-apellido1') || '';
            apellido2.value = button.getAttribute('data-apellido2') || '';
            nacimiento.value = button.getAttribute('data-nacimiento') || '';
            fallecimiento.value = button.getAttribute('data-fallecimiento') || '';
            lugar.value = button.getAttribute('data-lugar') || '';
            causa.value = button.getAttribute('data-causa') || '';
            cargarDireccion(button.getAttribute('data-provincia'), button.getAttribute('data-canton'),
                    button.getAttribute('data-distrito'));
            sennas.value = button.getAttribute('data-sennas') || '';
        } else {
            // Sin provincia escogida no se muestran cantones ni distritos
            cargarDireccion('', '', '');
        }
    });
});
