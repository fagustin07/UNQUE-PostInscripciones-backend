package ar.edu.unq.postinscripciones.model

enum class EstadoFormulario {
    ABIERTO{
        override fun cambiarEstado(formulario: Formulario){
            formulario.cerrarFormulario()
        }
   },
    CERRADO {
        override fun cambiarEstado(formulario: Formulario) {
            formulario.abrirFormulario()
        }
    };

    abstract fun cambiarEstado(formulario: Formulario)
}
