package p2;

import java.util.concurrent.Semaphore;

public class MainP2 {
	public static void main(String[] args) throws InterruptedException {
        System.out.println("=== DEMONSTRAÇÃO P2: SINCRONISMO, DEADLOCK E BANQUEIRO ===\n");

        // 1. CENÁRIO COM FALHA (Condição de Corrida)
        System.out.println(" 1. Executando Cenário com Falha (Condição de Corrida)");
        BufferCompartilhado bufferComFalha = new BufferCompartilhado(5, false);

        Thread produtor1 = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) bufferComFalha.produzir(i, "Produtor-1");
            } catch (InterruptedException e) {}
        });

        Thread consumidor1 = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) bufferComFalha.consumir("Consumidor-1");
            } catch (InterruptedException e) {}
        });

        produtor1.start();
        consumidor1.start();
        produtor1.join();
        consumidor1.join();
        System.out.println("Fim do cenário com falha (dados inconsistentes ou perdidos).\n");

        // 2. DEMONSTRAÇÃO DE DEADLOCK (Recursos Cruzados)
        System.out.println(" 2. Demonstrando Deadlock (Recursos Cruzados)");
        Object recursoA = new Object();
        Object recursoB = new Object();

        Thread t1 = new Thread(() -> {
            synchronized (recursoA) {
                System.out.println("Thread 1 segurou o Recurso A e quer o B...");
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                synchronized (recursoB) { System.out.println("Thread 1 pegou os dois recursos."); }
            }
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            synchronized (recursoB) {
                System.out.println("Thread 2 segurou o Recurso B e quer o A...");
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                synchronized (recursoA) { System.out.println("Thread 2 pegou os dois recursos."); }
            }
        }, "Thread-2");

        t1.start();
        t2.start();

        Thread watchdog = new Thread(() -> {
            try {
                Thread.sleep(1000);
                if (t1.isAlive() && t2.isAlive()) {
                    System.out.println("\n[WATCHDOG] ALERTA: Deadlock detectado!");
                    System.out.println("[WATCHDOG] Thread-1 segura Recurso A e espera B.");
                    System.out.println("[WATCHDOG] Thread-2 segura Recurso B e espera A.");
                }
            } catch (InterruptedException e) {}
        });
        watchdog.start();
        
        t1.join(2000);
        t2.join(2000);

        // 3. MÓDULO BÔNUS: ALGORITMO DO BANQUEIRO
        System.out.println("\n 3. Módulo Bônus: Algoritmo do Banqueiro (Evitação)");
        executarAlgoritmoBanqueiro();
    }

    // Implementação do Algoritmo do Banqueiro com os dados do exercício do enunciado[cite: 3]
    public static void executarAlgoritmoBanqueiro() {
        int numProcessos = 5;
        int numRecursos = 3;

        // Instâncias totais: A=10, B=5, C=7[cite: 3]
        int[] totalRecursos = {10, 5, 7};
        
        // Matriz de Alocação (A B C)[cite: 3]
        int[][] alocacao = {
            {0, 1, 0}, // P0
            {2, 0, 0}, // P1
            {3, 0, 2}, // P2
            {2, 1, 1}, // P3
            {0, 0, 2}  // P4
        };

        // Matriz Máxima (A B C)[cite: 3]
        int[][] maximo = {
            {7, 5, 3}, // P0
            {3, 2, 2}, // P1
            {9, 0, 2}, // P2
            {2, 2, 2}, // P3
            {4, 3, 3}  // P4
        };

        // 1. Calcular Matriz Necessidade (Maximo - Alocacao) e Vetor Disponível
        int[][] necessidade = new int[numProcessos][numRecursos];
        int[] alocadosTotais = new int[numRecursos];

        for (int i = 0; i < numProcessos; i++) {
            for (int j = 0; j < numRecursos; j++) {
                necessidade[i][j] = maximo[i][j] - alocacao[i][j];
                alocadosTotais[j] += alocacao[i][j];
            }
        }

        int[] disponivel = new int[numRecursos];
        for (int j = 0; j < numRecursos; j++) {
            disponivel[j] = totalRecursos[j] - alocadosTotais[j];
        }

        System.out.println("Vetor Disponível inicial: [" + disponivel[0] + ", " + disponivel[1] + ", " + disponivel[2] + "]");

        // 2. Verificar Estado Seguro
        boolean[] marcado = new boolean[numProcessos];
        int[] sequenciaSegura = new int[numProcessos];
        int count = 0;
        int[] trabalho = disponivel.clone();

        while (count < numProcessos) {
            boolean encontrou = false;
            for (int i = 0; i < numProcessos; i++) {
                if (!marcado[i]) {
                    boolean podeAtender = true;
                    for (int j = 0; j < numRecursos; j++) {
                        if (necessidade[i][j] > trabalho[j]) {
                            podeAtender = false;
                            break;
                        }
                    }
                    if (podeAtender) {
                        for (int j = 0; j < numRecursos; j++) {
                            trabalho[j] += alocacao[i][j];
                        }
                        sequenciaSegura[count++] = i;
                        marcado[i] = true;
                        encontrou = true;
                    }
                }
            }
            if (!encontrou) break;
        }

        if (count == numProcessos) {
            System.out.print("O sistema ESTÁ em estado seguro! Sequência segura: ");
            for (int i = 0; i < numProcessos; i++) {
                System.out.print("P" + sequenciaSegura[i] + (i < numProcessos - 1 ? " -> " : ""));
            }
            System.out.println();
        } else {
            System.out.println("O sistema NÃO está em estado seguro.");
        }
    }
}
