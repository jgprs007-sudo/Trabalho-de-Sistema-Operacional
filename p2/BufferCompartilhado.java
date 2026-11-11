package p2;

import java.util.concurrent.Semaphore;

public class BufferCompartilhado {
	private final int[] buffer;
    private int cabeca = 0;
    private int cauda = 0;
    private int contadorItens = 0;
    private final int capacidade;

    // Semáforos para o cenário corrigido
    private Semaphore mutex;
    private Semaphore vagasDisponiveis;
    private Semaphore itensDisponiveis;
    private boolean modoCorrigido;

    public BufferCompartilhado(int capacidade, boolean modoCorrigido) {
        this.capacidade = capacidade;
        this.buffer = new int[capacidade];
        this.modoCorrigido = modoCorrigido;
        
        // Inicializa os semáforos se estiver no modo corrigido
        if (modoCorrigido) {
            this.mutex = new Semaphore(1); // Exclusão mútua na região crítica
            this.vagasDisponiveis = new Semaphore(capacidade); // Espaços vazios
            this.itensDisponiveis = new Semaphore(0); // Itens para consumir
        }
    }

    public void produzir(int item, String nomeProdutor) throws InterruptedException {
        if (modoCorrigido) {
        	
            // Estratégia corrigida: aguarda vaga e garante exclusão mútua
            vagasDisponiveis.acquire();
            mutex.acquire();
            try {
                buffer[cauda] = item;
                cauda = (cauda + 1) % capacidade;
                contadorItens++;
                System.out.println(nomeProdutor + " produziu o item " + item + " | Total no buffer: " + contadorItens);
            } finally {
                mutex.release();
                itensDisponiveis.release();
            }
        } else {
        	
            // Cenário com falha: Condição de corrida intencional (sem proteção)
            Thread.sleep(10); // Simula processamento para forçar a intercalação
            buffer[cauda] = item;
            cauda = (cauda + 1) % capacidade;
            contadorItens++; // Variável compartilhada modificada sem exclusão mútua
            System.out.println(nomeProdutor + " produziu o item " + item + " | Contador (com falha): " + contadorItens);
        }
    }

    public void consumir(String nomeConsumidor) throws InterruptedException {
        if (modoCorrigido) {
        	
            // Estratégia corrigida: aguarda item disponível e protege a região crítica
            itensDisponiveis.acquire();
            mutex.acquire();
            try {
                int item = buffer[cabeca];
                cabeca = (cabeca + 1) % capacidade;
                contadorItens--;
                System.out.println(nomeConsumidor + " consumiu o item " + item + " | Total no buffer: " + contadorItens);
            } finally {
                mutex.release();
                vagasDisponiveis.release();
            }
        } else {
        	
            // Cenário com falha: Leitura sem proteção
            Thread.sleep(15);
            int item = buffer[cabeca];
            cabeca = (cabeca + 1) % capacidade;
            contadorItens--;
            System.out.println(nomeConsumidor + " consumiu o item " + item + " | Contador (com falha): " + contadorItens);
        }
    }

    public int getContadorItens() {
        return contadorItens;
    }
}

