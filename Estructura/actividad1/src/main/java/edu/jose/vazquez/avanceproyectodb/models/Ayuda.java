package edu.jose.vazquez.avanceproyectodb.models;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ayuda {

    // Tipos como en la UI de la captura
    public static final String[] TIPOS_PROBLEMA = new String[] {
        "Técnico", "Soporte", "Consulta", "Sugerencia", "Otro"
    };

    /** Entrada de FAQ */
    public static class FaqItem {
        public final String pregunta;
        public final String respuesta;
        public final String[] etiquetas;

        public FaqItem(String pregunta, String respuesta, String... etiquetas) {
            this.pregunta = Objects.requireNonNull(pregunta);
            this.respuesta = Objects.requireNonNull(respuesta);
            this.etiquetas = (etiquetas == null) ? new String[0] : etiquetas;
        }

        public boolean matches(String q) {
            if (q == null || q.isBlank()) return true;
            String n = q.toLowerCase();
            if (pregunta.toLowerCase().contains(n) || respuesta.toLowerCase().contains(n)) return true;
            for (String e : etiquetas) if (e != null && e.toLowerCase().contains(n)) return true;
            return false;
        }

        @Override public String toString() { return pregunta; }
    }

    /** Modelo de ticket simple */
    public static class Ticket {
        public String nombre, email, telefono, tipoProblema, descripcion;
        @Override public String toString() {
            return "Ticket{nombre='"+nombre+"', email='"+email+"', tel='"+telefono+
                   "', tipo='"+tipoProblema+"', desc='"+descripcion+"'}";
        }
    }

    /** Callback opcional para integrar envío real */
    public interface OnEnviarTicket { void enviar(Ticket t) throws Exception; }

    private final List<FaqItem> faq;
    private OnEnviarTicket onEnviar;

    public Ayuda() { this.faq = defaultFaq(); }

    public List<FaqItem> getFaq() { return new ArrayList<>(faq); }

    public void setOnEnviarTicket(OnEnviarTicket cb) { this.onEnviar = cb; }

    public OnEnviarTicket getOnEnviarTicket() { return onEnviar; }

    public static void openLink(String url) {
        try { if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(new URI(url)); }
        catch (Exception ignored) {}
    }

    // ==== FAQ que aparecen en tu imagen ====
    private static List<FaqItem> defaultFaq() {
        List<FaqItem> l = new ArrayList<>();
        l.add(new FaqItem(
            "¿Cómo puedo contactar con EmergenSys S.A. de C.V.?",
            "En la sección Ayuda, completa el formulario y pulsa ‘Correo’. También puedes escribir a soporte@emergensys.mx.",
            "contacto","correo","soporte"
        ));
        l.add(new FaqItem(
            "¿Cómo asigno un doctor por especialidad?",
            "En Atención, elige la especialidad; después se habilita el combo de doctores filtrado por esa especialidad.",
            "atencion","especialidad","doctores"
        ));
        l.add(new FaqItem(
            "¿Dónde veo el historial de atenciones?",
            "En Atención, usa el botón ‘Historial’ del paciente seleccionado para ver sus atenciones previas.",
            "historial","paciente","atenciones"
        ));
        l.add(new FaqItem(
            "¿Cómo editar un doctor?",
            "Ve a Doctores, selecciona el registro en la tabla, edita los campos y pulsa ‘Guardar cambios’.",
            "doctores","editar","guardar"
        ));
        l.add(new FaqItem(
            "¿Por qué el estatus del paciente no cambia?",
            "Si el paciente fue dado de alta en Atención, ya no puedes cambiar su estado desde Pacientes.",
            "pacientes","estado","alta"
        ));
        l.add(new FaqItem(
            "¿Qué hace “Agregar LADA”?",
            "Permite capturar la clave telefónica (LADA) por separado para normalizar teléfonos: (LADA) número.",
            "lada","telefono"
        ));
        l.add(new FaqItem(
            "¿Dónde escribo el teléfono?",
            "En Pacientes, usa los campos LADA y Teléfono. Solo se permiten dígitos y se formatea como (LADA) número.",
            "telefono","lada","validacion"
        ));
        l.add(new FaqItem(
            "¿Cómo guardo un reporte?",
            "En Reportes puedes consultar métricas. Para exportar, usa las opciones del panel si están disponibles.",
            "reportes","exportar","guardar"
        ));
        l.add(new FaqItem(
            "¿Qué hago si “Guardar reporte” marca error?",
            "Revisa conexión a BD y permisos. Si persiste, envía un ticket desde Ayuda con la descripción del error.",
            "error","reportes","bd"
        ));
        return l;
    }
}


