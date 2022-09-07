package bantads.conta.exception;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static bantads.conta.config.RabbitMQConfig.CHAVE_MENSAGEM;
import static bantads.conta.config.RabbitMQConfig.MENSAGEM_EXCHANGE;

public class ContaException extends ResponseStatusException {

    public ContaException(HttpStatus status, String reason, AmqpTemplate rabbitTemplate) {
        super(status, reason);
        rabbitTemplate.convertAndSend(MENSAGEM_EXCHANGE, CHAVE_MENSAGEM, "error");
    }
}
