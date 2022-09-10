package bantads.conta.controller;

import bantads.conta.model.Conta;
import bantads.conta.model.Movimentacao;
import bantads.conta.service.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ContaController {

    @Autowired
    ContaService contaService;

    @GetMapping("/")
    public ResponseEntity<List<Conta>> findAll() {
        return ResponseEntity.ok().body(contaService.findAll());
    }
    
    @GetMapping("/{idCliente}")
    public ResponseEntity<Optional<Conta>> getByIdCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok().body(contaService.getByIdCliente(idCliente));
    }

    @PostMapping("/")
    public ResponseEntity<Conta> save(@RequestBody Conta conta) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contaService.insert(conta));
    }

    @PutMapping("/deposito/{idCliente}")
    public ResponseEntity<Object> depositar(@PathVariable Long idCliente, @RequestParam Long valor){
        contaService.depositar(idCliente, valor);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/saque/{idCliente}")
    public ResponseEntity<Object> sacar(@PathVariable Long idCliente, @RequestParam Long valor){
        contaService.sacar(idCliente, valor);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/transferencia/{origem}/{destino}")
    public ResponseEntity<Object> transferir(@PathVariable Long origem,
                                             @PathVariable Long destino,
                                             @RequestParam Long valor){
        contaService.transferir(origem, destino, valor);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/extrato/{idCliente}")
    public ResponseEntity<List<Movimentacao>> extrato(@PathVariable Long idCliente){
        return ResponseEntity.ok().body(contaService.extrato(idCliente));
    }

    @DeleteMapping("/{idCliente}")
    public ResponseEntity<String> deleteByIdCliente(@PathVariable Long idCliente){
        return ResponseEntity.ok(contaService.deleteByIdCliente(idCliente));
    }

}
