package p3;

import java.util.*;

public class SimuladorMemoria {
	public static void main(String[] args) {
        System.out.println("=== SIMULADOR DE MEMÓRIA PAGINADA (P3 COMPLETO + BÔNUS) ===\n");

        // ETAPA 1
        System.out.println(" ETAPA 1: Partições Variáveis");
        simularParticoesVariaveis();

        // ETAPA 2
        System.out.println("\n ETAPA 2: Tabela de Páginas e Tradução");
        simularTabelaPaginas();

        // ETAPA 3 (FIFO, LRU e Relógio Bônus)
        System.out.println("\n ETAPA 3: Substituição de Páginas (FIFO, LRU e Relógio)");
        simularSubstituicaoPaginas();
    }

    public static void simularParticoesVariaveis() {
        List<Integer> buracos = new ArrayList<>(Arrays.asList(100, 500, 200, 300, 600));
        int processoTamanho = 212;
        System.out.println("Buracos livres: " + buracos + " | Processo: " + processoTamanho + " KB");
        
        // First-Fit
        for (int i = 0; i < buracos.size(); i++) {
            if (buracos.get(i) >= processoTamanho) {
                System.out.println("[First-Fit] Alocado no buraco de índice " + i + " (tamanho " + buracos.get(i) + " KB)");
                break;
            }
        }
    }

    public static void simularTabelaPaginas() {
        int tamanhoPagina = 4096;
        int enderecoVirtual = 20500;
        int paginaVirtual = enderecoVirtual / tamanhoPagina;
        int deslocamento = enderecoVirtual % tamanhoPagina;
        int frameFisico = 7; //[cite: 6]
        int enderecoFisico = (frameFisico * tamanhoPagina) + deslocamento;

        System.out.println("Endereço Virtual: " + enderecoVirtual + " -> Frame Físico: " + frameFisico + " -> Endereço Físico: " + enderecoFisico);
        System.out.println("Bit de validade: 1 | Dirty Bit: 0[cite: 6]");
    }

    public static void simularSubstituicaoPaginas() {
        int[] referencias = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2, 1, 2, 0, 1, 7, 0, 1}; //[cite: 6]
        int numFrames = 3; //[cite: 6]

        simularFIFO(referencias, numFrames);
        simularLRU(referencias, numFrames);
        simularRelogio(referencias, numFrames); // Bônus
    }

    private static void simularFIFO(int[] refs, int framesCap) {
        Queue<Integer> fila = new LinkedList<>();
        Set<Integer> memoria = new HashSet<>();
        int pageFaults = 0;

        for (int ref : refs) {
            if (!memoria.contains(ref)) {
                pageFaults++;
                if (memoria.size() >= framesCap) {
                    int removido = fila.poll();
                    memoria.remove(removido);
                }
                memoria.add(ref);
                fila.add(ref);
            }
        }
        System.out.println("Total Page Faults (FIFO): " + pageFaults);
    }

    private static void simularLRU(int[] refs, int framesCap) {
        LinkedHashMap<Integer, Integer> memoriaLRU = new LinkedHashMap<>(framesCap, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > framesCap;
            }
        };
        int pageFaults = 0;

        for (int ref : refs) {
            if (!memoriaLRU.containsKey(ref)) pageFaults++;
            memoriaLRU.put(ref, 1);
        }
        System.out.println("Total Page Faults (LRU): " + pageFaults);
    }

    // Algoritmo do Relógio (Second-Chance) - Bônus
    private static void simularRelogio(int[] refs, int framesCap) {
        int[] frames = new int[framesCap];
        int[] bitReferencia = new int[framesCap];
        Arrays.fill(frames, -1);
        int ponteiro = 0;
        int pageFaults = 0;

        for (int ref : refs) {
            boolean encontrado = false;
            // Verifica se já está na memória
            for (int i = 0; i < framesCap; i++) {
                if (frames[i] == ref) {
                    bitReferencia[i] = 1; // Dá uma segunda chance / marca como referenciado
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                pageFaults++;
                // Procura vítima usando o ponteiro do relógio
                while (true) {
                    if (bitReferencia[ponteiro] == 0) {
                        frames[ponteiro] = ref;
                        bitReferencia[ponteiro] = 0;
                        ponteiro = (ponteiro + 1) % framesCap;
                        break;
                    } else {
                        bitReferencia[ponteiro] = 0; // Zera o bit e passa para o próximo
                        ponteiro = (ponteiro + 1) % framesCap;
                    }
                }
            }
        }
        System.out.println("Total Page Faults (Algoritmo do Relógio - Bônus): " + pageFaults);
    }
}
