# Calculadora RMI — IFSULDEMINAS Campus Machado
### Sistemas Computacionais Distribuídos — Nota de Aula 15

---

## Estrutura do projeto

```
CalculadoraRMI/
├── src/
│   └── br/edu/ifsuldeminas/mch/sd/rmi/
│       ├── remote/
│       │   └── Operations.java        ← Interface remota (todas as operações)
│       ├── server/
│       │   ├── Calculator.java        ← Implementação das operações
│       │   └── Server.java            ← Sobe o serviço RMI
│       └── client/
│           ├── CalculatorGUI.java     ← Interface gráfica (tema escuro)
│           └── Client.java            ← Cliente console (testes)
├── .project
├── .classpath
└── README.md
```

---

## Operações implementadas

| Categoria     | Operação                          |
|---------------|-----------------------------------|
| Básico        | Soma, Subtração, Multiplicação, Divisão |
| Raiz          | Quadrada, cúbica, n-ésima grau    |
| Potência      | Base^Expoente                     |
| Porcentagem   | X% de Y                           |
| Módulo        | A mod B                           |
| Fatorial      | n! (com tratamento de erros)      |
| Conversão     | Decimal ↔ Binário ↔ Hexadecimal   |
| Histórico     | Últimas N operações               |

---

## Como importar no Eclipse

1. Abra o Eclipse
2. **File → Import → General → Existing Projects into Workspace**
3. Clique em **Browse** e selecione a pasta `CalculadoraRMI`
4. Marque o projeto e clique em **Finish**
5. O projeto deve aparecer no Package Explorer com todos os arquivos

---

## Como rodar no Eclipse

### Passo 1 — Iniciar o Servidor

1. No Package Explorer, navegue até:  
   `src → br.edu.ifsuldeminas.mch.sd.rmi.server → Server.java`
2. Clique com botão direito → **Run As → Java Application**
3. No console do Eclipse deve aparecer:
   ```
   ╔══════════════════════════════════════╗
   ║   Calculadora RMI - Servidor ativo   ║
   ║   Porta: 1099                        ║
   ╚══════════════════════════════════════╝
   ```
4. **Deixe este console aberto / rodando**

### Passo 2 — Abrir a Interface Gráfica

1. Navegue até:  
   `src → br.edu.ifsuldeminas.mch.sd.rmi.client → CalculatorGUI.java`
2. Clique com botão direito → **Run As → Java Application**
3. A janela da calculadora abrirá com tema escuro
4. Na barra inferior deve aparecer **"Conectado ao servidor RMI ✓"**

### (Opcional) Passo 2b — Rodar cliente console

1. Navegue até:  
   `src → br.edu.ifsuldeminas.mch.sd.rmi.client → Client.java`
2. Clique com botão direito → **Run As → Java Application**
3. Os resultados de todas as operações serão exibidos no console

---

## Observações importantes

- O **Servidor deve ser iniciado primeiro**, antes de qualquer cliente
- Ambos rodam na mesma máquina (`localhost`) via porta **1099** (padrão RMI)
- Requer **Java 8 ou superior**
- Não é necessário nenhuma biblioteca externa — apenas o JDK padrão
- O histórico de operações é mantido no servidor e atualizado em tempo real
- Fatorial de n > 20 usa `BigInteger` internamente para evitar overflow

---

## Tratamento de erros

| Situação                    | Comportamento                        |
|-----------------------------|--------------------------------------|
| Divisão por zero            | Retorna `NaN`, exibe mensagem        |
| Raiz par de número negativo | Retorna `NaN`, exibe mensagem        |
| Fatorial de negativo        | Retorna `NaN`, exibe mensagem        |
| Servidor offline            | Mensagem de erro na barra de status  |
| Binário inválido            | Exceção capturada, exibe mensagem    |
