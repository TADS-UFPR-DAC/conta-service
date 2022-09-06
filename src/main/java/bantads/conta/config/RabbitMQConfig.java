package bantads.conta.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String NOME_EXCHANGE = "conta";
    public static final String FILA_SALVAR_CONTA = "CriarContaQueue";
    public static final String FILA_SALVAR_MOVIMENTACAO = "CriarMovQueue";
    public static final String FILA_DELETAR_CONTA = "DeletarContaQueue";
    public static final String CHAVE_SALVAR_CONTA = "criarConta";
    public static final String CHAVE_SALVAR_MOVIMENTACAO = "criarMov";
    public static final String CHAVE_DELETAR_CONTA = "deletarConta";

    @Bean
    DirectExchange contaExchange() {
        return new DirectExchange(NOME_EXCHANGE);
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
}
