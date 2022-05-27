package ar.edu.unq.postinscripciones.persistence

import ar.edu.unq.postinscripciones.model.Alumno
import ar.edu.unq.postinscripciones.model.EstadoMateria
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.Tuple

@Repository
interface AlumnoRepository : CrudRepository<Alumno, Int> {
    fun findByDniOrLegajo(dni: Int, legajo: Int): Optional<Alumno>
    fun findByDni(dni: Int): Optional<Alumno>

    @Query(
        "SELECT alu.dni, afs.formulario_id, afs.solicitudes_id, count(aha.historia_academica_id) AS aprobadas\n" +
                "FROM alumno AS alu\n" +
                "JOIN (SELECT alu.dni, fs.*\n" +
                "                FROM alumno AS alu\n" +
                "                JOIN alumno_formularios AS af\n" +
                "                ON alu.dni = af.alumno_dni\n" +
                "                JOIN formulario_solicitudes AS fs\n" +
                "                ON fs.formulario_id = af.formularios_id\n" +
                "                JOIN solicitud_sobrecupo AS ss\n" +
                "                ON ss.id = fs.solicitudes_id AND ss.comision_id = :idComision) AS afs\n" +
                "ON afs.dni = alu.dni\n" +
                "LEFT JOIN alumno_historia_academica AS aha\n" +
                "ON aha.alumno_dni = alu.dni\n" +
                "LEFT JOIN materia_cursada AS mc\n" +
                "ON mc.id = aha.historia_academica_id AND mc.estado = :estadoMateria\n" +
                "GROUP BY alu.dni\n" +
                "ORDER BY aprobadas DESC", nativeQuery = true
    )
    fun findBySolicitaComisionIdOrderByCantidadAprobadas(idComision: Long, estadoMateria: String = EstadoMateria.APROBADO.toString()): List<Tuple>

    @Query(
        "SELECT cursada.materia_codigo, cursada.estado, info.fecha, info.intentos " +
        "FROM materia_cursada AS cursada " +
        "JOIN " +
            "(SELECT alumno_dni, materia_codigo, max(fecha_de_carga) as fecha, count(*) AS intentos " +
            "FROM materia_cursada " +
            "JOIN alumno_historia_academica " +
            "ON historia_academica_id = id " +
            "WHERE alumno_dni = ?1 " +
            "GROUP BY alumno_dni, materia_codigo) AS info " +
        "ON info.materia_codigo = cursada.materia_codigo " +
        "WHERE info.fecha = cursada.fecha_de_carga",
        nativeQuery = true
    )
    fun findResumenHistoriaAcademica(dni: Int): List<Tuple>
}