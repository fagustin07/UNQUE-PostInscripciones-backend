package ar.edu.unq.postinscripciones.service.dto.alumno

import ar.edu.unq.postinscripciones.service.dto.formulario.FormularioCrearAlumno

data class ConflictoAlumnoDTO(val alumno: AlumnoDTO, val formularioConflictivo: FormularioCrearAlumno)