package ar.edu.unq.postinscripciones.model.exception

open class RecursoNoEncontrado(mensaje: String) : ExcepcionUNQUE(mensaje)


class MateriaNoEncontrada(codigo: String): RecursoNoEncontrado("La materia con codigo ${codigo} no se encuentra registrada en el sistema")

class CuatrimestreNoEncontrado(): RecursoNoEncontrado("El cuatrimestre no existe")

class ComisionNoEncontrada(idComision: Long): RecursoNoEncontrado("La comision con id ${idComision} no existe")

class AlumnoNoEncontrado(alumnoDni: Int): RecursoNoEncontrado("El alumno con dni ${alumnoDni} no existe")

class FormularioNoEncontrado(formularioId: Long): RecursoNoEncontrado("El formulario con id ${formularioId} no existe")
