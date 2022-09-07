package bantads.conta.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String CONTA_EXCHANGE = "conta";
    public static final String MENSAGEM_EXCHANGE = "mensagem";
    public static final String FILA_SALVAR_CONTA = "CriarContaQueue";
    public static final String FILA_SALVAR_MOVIMENTACAO = "CriarMovQueue";
    public static final String FILA_DELETAR_CONTA = "DeletarContaQueue";
    public static final String FILA_MENSAGEM = "MensagemQueue";
    public static final String CHAVE_SALVAR_CONTA = "criarConta";
    public static final String CHAVE_SALVAR_MOVIMENTACAO = "criarMov";
    public static final String CHAVE_DELETAR_CONTA = "deletarConta";
    public static final String CHAVE_MENSAGEM = "mensagem";

    @Bean
    DirectExchange contaExchange() {
        return new DirectExchange(CONTA_EXCHANGE);
    }

    @Bean
    DirectExchange mensagemExchange() {
        return new DirectExchange(MENSAGEM_EXCHANGE);
    }

    @Bean
    Queue criarContaQueue() {
        return QueueBuilder.durable(FILA_SALVAR_CONTA).build();
    }

    @Bean
    Queue criarMovimentacaoQueue() {
        return QueueBuilder.durable(FILA_SALVAR_MOVIMENTACAO).build();
    }

    @Bean
    Queue deletarContaQueue() {
        return QueueBuilder.durable(FILA_DELETAR_CONTA).build();
    }

    @Bean
    Queue mensagemQueue() {
        return QueueBuilder.durable(FILA_MENSAGEM).build();
    }

    @Bean
    Binding criarContaBinding() {
        return BindingBuilder.bind(criarContaQueue()).to(contaExchange()).with(CHAVE_SALVAR_CONTA);
    }

    @Bean
    Binding criarMovimentacaoBinding() {
        return BindingBuilder.bind(criarMovimentacaoQueue()).to(contaExchange()).with(CHAVE_SALVAR_MOVIMENTACAO);
    }

    @Bean
    Binding deletarContaBinding() {
        return BindingBuilder.bind(deletarContaQueue()).to(contaExchange()).with(CHAVE_DELETAR_CONTA);
    }

    @Bean
    Binding mensagemBinding() {
        return BindingBuilder.bind(mensagemQueue()).to(mensagemExchange()).with(CHAVE_MENSAGEM);
    }
}
