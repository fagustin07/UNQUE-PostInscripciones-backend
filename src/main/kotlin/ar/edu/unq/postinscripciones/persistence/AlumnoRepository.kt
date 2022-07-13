package ar.edu.unq.postinscripciones.persistence

import ar.edu.unq.postinscripciones.model.Alumno
import ar.edu.unq.postinscripciones.model.EstadoMateria
import ar.edu.unq.postinscripciones.model.EstadoSolicitud
import ar.edu.unq.postinscripciones.model.cuatrimestre.Semestre
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.Tuple

@Repository
interface AlumnoRepository : CrudRepository<Alumno, Int> {

    @Query(
        "SELECT a " +
        "FROM Alumno as a " +
        "LEFT JOIN MateriaCursada as m " +
            "ON m.id IN ( " +
            "SELECT m2.id FROM a.historiaAcademica as m2 WHERE m2.estado = ?2 " +
        ") " +
        "WHERE CAST(dni as string) LIKE concat(?1, '%') " +
        "GROUP BY a.dni " +
        "ORDER BY count(m) DESC"
    )
    fun findByDniStartsWithOrderByCantAprobadasDesc(dniString: String, estadoMateria: EstadoMateria = EstadoMateria.APROBADO ): List<Alumno>

    @Query(
        "SELECT alu.dni, afs.formulario_id, afs.solicitud_id, count(mc.id) AS aprobadas\n" +
                "FROM alumno AS alu\n" +
                "JOIN (SELECT alu.dni, ss.id as solicitud_id, ss.formulario_id \n" +
                "                FROM alumno AS alu\n" +
                "                JOIN alumno_formularios AS af\n" +
                "                ON alu.dni = af.alumno_dni\n" +
                "                JOIN solicitud_sobrecupo AS ss\n" +
                "                ON af.formularios_id = ss.formulario_id AND ss.comision_id = :idComision) AS afs\n" +
                "ON afs.dni = alu.dni\n" +
                "LEFT JOIN materia_cursada AS mc\n" +
                "ON mc.id = alu.dni AND mc.estado = :estadoMateria\n" +
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
            "WHERE alumno_dni = ?1 " +
            "GROUP BY alumno_dni, materia_codigo) AS info " +
        "ON info.materia_codigo = cursada.materia_codigo " +
        "WHERE info.fecha = cursada.fecha_de_carga " +
        "GROUP BY cursada.materia_codigo",
        nativeQuery = true
    )
    fun findResumenHistoriaAcademica(dni: Int): List<Tuple>
    @Query(
        "SELECT a.dni, a.nombre, a.apellido, f.id, s.id, s.comision.numero, s.comision.materia.codigo, count(m) as materias_aprobadas, s.estado " +
        "FROM Alumno as a " +
        "JOIN Formulario as f " +
            "ON f.id IN (SELECT f2.id FROM a.formularios as f2) " +
        "JOIN SolicitudSobrecupo as s " +
            "ON s.id IN ( " +
                "SELECT s2.id FROM f.solicitudes as s2 WHERE s2.comision.materia.codigo = ?1 AND (?2 IS NULL OR s2.comision.numero = ?2)" +
            ") " +
        "LEFT JOIN MateriaCursada as m " +
            "ON m.id IN ( " +
                "SELECT m2.id FROM a.historiaAcademica as m2 WHERE m2.estado = ?6 " +
            ") " +
        "WHERE f.cuatrimestre.semestre = ?3 AND f.cuatrimestre.anio = ?4 AND (?5 IS NULL OR (?5 IS TRUE AND s.estado = ?7) OR (?5 IS FALSE AND NOT s.estado = ?7)) " +
        "GROUP BY a.dni, f.id, s.id, s.comision.numero, s.comision.materia.codigo " +
        "ORDER BY count(m) DESC"
    )
    fun findBySolicitaMateriaAndComisionMOrderByCantidadAprobadas(codigo: String, numero : Int?, semestre: Semestre, anio: Int, pendiente: Boolean?,estado : EstadoMateria = EstadoMateria.APROBADO, estadoSolicitud: EstadoSolicitud = EstadoSolicitud.PENDIENTE): List<Tuple>

    @Query(
        "SELECT a.dni, a.nombre, a.apellido, a.correo, f.id, f.estado, f.comisionesInscripto.size as total_materias_inscripto, count(solicitudes_pendientes) as total_solicitudes_pendientes, count(solicitudes_aprobadas) as total_solicitudes_aprobadas, count(m) " +
        "FROM Alumno as a " +
            "JOIN Formulario as f " +
                "ON f.id IN (SELECT f2.id FROM a.formularios as f2 WHERE f2.cuatrimestre.semestre = ?2 AND f2.cuatrimestre.anio = ?3) " +
            "LEFT JOIN SolicitudSobrecupo as solicitudes_pendientes " +
                "ON solicitudes_pendientes.id IN ( " +
                    "SELECT s2.id  FROM f.solicitudes as s2 WHERE s2.estado = ?6 " +
                ") " +
            "LEFT JOIN SolicitudSobrecupo as solicitudes_aprobadas " +
                "ON solicitudes_aprobadas.id IN ( " +
                    "SELECT solicitudes.id FROM f.solicitudes as solicitudes WHERE solicitudes.estado = ?7 " +
                ") " +
            "LEFT JOIN MateriaCursada as m " +
                "ON m.id IN ( " +
                    "SELECT m2.id FROM a.historiaAcademica as m2 WHERE m2.estado = ?7 " +
                ") " +
        "WHERE (?1 IS NULL OR concat(a.dni, '') LIKE %?1% ) " +
        "GROUP BY a.dni, a.nombre, a.apellido, a.correo, f.id " +
        "HAVING (?4 IS NULL OR (?4 IS TRUE AND f.solicitudes.size = count(solicitudes_pendientes)) OR (?4 IS FALSE AND NOT f.solicitudes.size = count(solicitudes_pendientes))) " +
            "AND (?5 IS NULL OR (?5 IS TRUE AND count(solicitudes_pendientes) > 0) OR (?5 IS FALSE AND count(solicitudes_pendientes) = 0)) " +
        "ORDER BY count(m) DESC"
    )
    fun findAllByDni(dni: String?, semestre: Semestre, anio: Int, sinProcesar: Boolean? = null, pendiente: Boolean? = null, estado : EstadoSolicitud = EstadoSolicitud.PENDIENTE, estadoAprobado : EstadoSolicitud = EstadoSolicitud.APROBADO): List<Tuple>
}