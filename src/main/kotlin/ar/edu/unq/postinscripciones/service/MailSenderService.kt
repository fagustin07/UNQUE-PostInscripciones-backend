package ar.edu.unq.postinscripciones.service

import ar.edu.unq.postinscripciones.service.dto.alumno.AlumnoDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import javax.mail.MessagingException

@Service
class MailSenderService {

    @Autowired
    private lateinit var javaMailSender: JavaMailSender

    @Async
    fun enviarCodigo(alumno: AlumnoDTO, codigo: Int) {
        sendMail(
            alumno.correo,
            """
                   <html>
                    <body>
                        <div>
                            <div> Hola ${alumno.nombre} ${alumno.apellido},</div>
                            <div> Su código de confirmación de cuenta es <strong>$codigo</strong>. Úselo para que 
                            pueda pedir solicitudes de sobrecupo.</div>
                        </div><br>
                        <div>
                            * Nota: El código perderá validez dentro de 5 minutos.<br><br>

                            Si no sabes por qué te ha llegado este correo, es posible que alguien haya intentado utilizar tus datos 
                            para crear una cuenta en el sistema de Postinscripciones. Te recomendamos contactar a los directivos.<br><br>

                            Si no eres el destinatario de este mensaje, elimínalo, por favor.
                            Si tienes alguna duda o pregunta relativa al servicio, comunícate con cpi@unque.edu.ar vía correo.<br><br>
                        </div>
                        <div>Equipo UNQUE</div>
                    </body>
                   </html>
                """.trimIndent()
        )
    }

    private fun sendMail(to: String, text: String) {
        val mailMessage = javaMailSender.createMimeMessage()
        val helper: MimeMessageHelper
        try {
            helper = MimeMessageHelper(mailMessage, false)
            helper.setTo(to)
            helper.setSubject("Confirme su cuenta de UNQUE-Postinscripciones")
            helper.setText(text, true)
            javaMailSender.send(mailMessage)
            println("[MAIL_SENDER] Correo de confirmación enviado a $to")
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }
}