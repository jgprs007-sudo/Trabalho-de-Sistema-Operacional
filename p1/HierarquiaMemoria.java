package p1;

import java.util.Random;

public class HierarquiaMemoria {
	
	public static void main(String[] args) {
		
        // Testando com três tamanhos diferentes conforme o enunciado
        int[] tamanhos = {1000, 2000, 3000};
        int medicoes = 5; // Repetir pelo menos 5 vezes para tirar a média[cite: 5]

        System.out.println("=== EXPERIMENTO DE HIERARQUIA DE MEMÓRIA (P1) ===\n");
        System.out.println(String.format("%-10s | %-20s | %-20s", "Tamanho", "Média Linha (ms)", "Média Coluna (ms)"));
        System.out.println("-------------------------------------------------------------");

        for (int tamanho : tamanhos) {
            int[][] matriz = new int[tamanho][tamanho];
            Random random = new Random();
            
            // Preenchendo a matriz
            for (int i = 0; i < tamanho; i++) {
                for (int j = 0; j < tamanho; j++) {
                    matriz[i][j] = random.nextInt(10);
                }
            }

            // TESTE 1: Percurso Linha a Linha (Boa localidade)
            long tempoTotalLinha = 0;
            for (long m = 0; m < medicoes; m++) {
                long inicio = System.nanoTime();
                
                long soma = 0;
                for (int i = 0; i < tamanho; i++) {
                    for (int j = 0; j < tamanho; j++) {
                        soma += matriz[i][j];
                    }
                }
                
                long fim = System.nanoTime();
                tempoTotalLinha += (fim - inicio) / 1_000_000;
            }
            double mediaLinha = (double) tempoTotalLinha / medicoes;

            // TESTE 2: Percurso Coluna a Coluna (Má localidade)
            long tempoTotalColuna = 0;
            for (long m = 0; m < medicoes; m++) {
                long inicio = System.nanoTime();
                
                long soma = 0;
                for (int j = 0; j < tamanho; j++) {
                    for (int i = 0; i < tamanho; i++) {
                        soma += matriz[i][j];
                    }
                }
                
                long fim = System.nanoTime();
                tempoTotalColuna += (fim - inicio) / 1_000_000;
            }
            double mediaColuna = (double) tempoTotalColuna / medicoes;

            // Exibindo a linha da tabela de resultados
            System.out.println(String.format("%-10s | %-20.2f | %-20.2f", (tamanho + "x" + tamanho), mediaLinha, mediaColuna));
        }
        
        System.out.println("\nExperimente rodar e anotar esses valores para montar o gráfico/tabela do relatório!");
    }
}
