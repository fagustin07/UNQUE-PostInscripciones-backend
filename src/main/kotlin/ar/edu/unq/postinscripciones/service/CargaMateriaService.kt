package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.model.*
import ar.edu.unq.postinscripciones.persistence.MateriaRepository
import ar.edu.unq.postinscripciones.service.dto.carga.datos.Conflicto
import ar.edu.unq.postinscripciones.service.dto.carga.datos.MateriaParaCargar
import ar.edu.unq.postinscripciones.service.dto.carga.datos.Plan
import ar.edu.unq.postinscripciones.service.dto.carga.datos.PlanillaMaterias
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class CargaMateriaService {

    @Autowired
    private lateinit var materiaRepository: MateriaRepository

    @Transactional
    fun cargarMaterias(planillaMaterias: PlanillaMaterias): List<Conflicto> {
        val conflictos = mutableListOf<Conflicto>()

        planillaMaterias.materias.forEach {
            val nuevosConflictos = guardarDatosBasicosMateria(it, planillaMaterias.plan)
            conflictos.addAll(nuevosConflictos)
        }

        planillaMaterias.materias.forEach {
            val nuevosConflictos = actualizarRequisitosMateria(it, planillaMaterias.plan)
            conflictos.addAll(nuevosConflictos)
        }

        return conflictos
    }

    private fun guardarDatosBasicosMateria(nuevaMateria: MateriaParaCargar, plan: Plan): List<Conflicto> {
        val conflictos = mutableListOf<Conflicto>()
        val existeMateria = materiaRepository.findMateriaByCodigo(nuevaMateria.codigo)
        val materia = if (existeMateria.isPresent) {
            conflictos.add(Conflicto(nuevaMateria.fila, "La materia ${nuevaMateria.codigo} existe y se actualizarán sus datos"))
            existeMateria.get()
        } else {
            Materia(
                nuevaMateria.codigo,
                nuevaMateria.materia,
                creditos = nuevaMateria.creditos,
                tpi2015 = CicloTPI.NO_PERTENECE,
                li = CicloLI.NO_PERTENECE,
                tpi2010 = CicloTPI.NO_PERTENECE,
            )
        }
        setearCiclo(plan, materia, nuevaMateria.cicloTPI, nuevaMateria.cicloLI)
        materiaRepository.save(materia)
        return conflictos
    }

    private fun actualizarRequisitosMateria(nuevaMateria: MateriaParaCargar, plan: Plan): List<Conflicto> {
        val conflictos = mutableListOf<Conflicto>()
        val materia = materiaRepository.findMateriaByCodigo(nuevaMateria.codigo).get()
        val correlativas = materiaRepository.findAllByCodigoIn(nuevaMateria.correlativas).toMutableList()
        if (correlativas.size != nuevaMateria.correlativas.size) {
            conflictos.add(Conflicto(nuevaMateria.fila, "Hay materias correlativas que se quisieron cargar y no existen"))
        }
        val  requisitosCiclo = crearRequisitos(nuevaMateria, plan)

        materia.actualizarRequisitos(correlativas, requisitosCiclo)

        materiaRepository.save(materia)

        return conflictos
    }

    private fun crearRequisitos(nuevaMateria: MateriaParaCargar, plan: Plan): MutableList<RequisitoCiclo> {
        return when (plan) {
            Plan.TPI2010 -> crearRequisitosTPI2010(nuevaMateria.co, nuevaMateria.ca)
            Plan.TPI2015 -> crearRequisitosTPI2015(nuevaMateria.ci, nuevaMateria.co, nuevaMateria.ca, nuevaMateria.cc)
            Plan.LI -> crearRequisitosLI(nuevaMateria.ci, nuevaMateria.nbw, nuevaMateria.cb)
        }

    }

    private fun setearCiclo(plan: Plan, materia: Materia, cicloTPI: CicloTPI, cicloLI: CicloLI) {
        when (plan) {
            Plan.TPI2010 -> materia.pertenecerATPI2010(cicloTPI)
            Plan.TPI2015 -> materia.pertenecerATPI2015(cicloTPI)
            Plan.LI -> materia.pertenecerALI(cicloLI)
        }
    }

    private fun crearRequisitosLI(ci: Int, nbw: Int, cb: Int): MutableList<RequisitoCiclo> {
        val requisitos = mutableListOf<RequisitoCiclo>()

        if (ci > 0) requisitos.add(crearRequisitoLI(ci,CicloLI.CI))
        if (nbw > 0) requisitos.add(crearRequisitoLI(nbw, CicloLI.NBW))
        if (cb > 0) requisitos.add(crearRequisitoLI(cb, CicloLI.CB))

        return requisitos
    }

    private fun crearRequisitosTPI2015(ci: Int, co: Int, ca: Int, cc: Int): MutableList<RequisitoCiclo> {
        val requisitos = mutableListOf<RequisitoCiclo>()

        if (ci > 0) requisitos.add(crearRequisitoTPI(ci, CicloTPI.CI, false))
        if (co > 0) requisitos.add(crearRequisitoTPI(co, CicloTPI.CO, false))
        if (ca > 0) requisitos.add(crearRequisitoTPI(ca, CicloTPI.CA, false))
        if (cc > 0) requisitos.add(crearRequisitoTPI(cc, CicloTPI.CC, false))

        return requisitos
    }

    private fun crearRequisitosTPI2010(co: Int, ca: Int): MutableList<RequisitoCiclo> {
        val requisitos = mutableListOf<RequisitoCiclo>()

        if (co > 0) requisitos.add(crearRequisitoTPI(co, CicloTPI.CO, true))
        if (ca > 0) requisitos.add(crearRequisitoTPI(ca, CicloTPI.CA, true))

        return requisitos
    }

    private fun crearRequisitoLI(creditos: Int, ciclo: CicloLI) = RequisitoCiclo(
        carrera = Carrera.W,
        cicloTPI = CicloTPI.NO_PERTENECE,
        cicloLI = ciclo,
        esTPI2010 = false,
        creditos = creditos
    )

    private fun crearRequisitoTPI(creditos: Int, ciclo: CicloTPI, esTPI2010: Boolean): RequisitoCiclo {
        return RequisitoCiclo(
            carrera = Carrera.P,
            cicloTPI = ciclo,
            cicloLI = CicloLI.NO_PERTENECE,
            esTPI2010 = esTPI2010,
            creditos = creditos
        )
    }
}