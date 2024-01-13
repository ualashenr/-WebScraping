package model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Produto {
	
	private String descricao;
	private String valorProdudos;
	private String parcelaProdutos;
	private String nome;
	private String vendedor;
	private Double avaliacao;
	private String opinioes;
	private BigDecimal valorAVista;
	private String parcelas;
	private BigDecimal valorAPrazo;
	
	public Produto(String descricao, String valorProdudos, String parcelaProdutos) {
		this.descricao = descricao;
		this.valorProdudos = valorProdudos;
		this.parcelaProdutos = parcelaProdutos;
		this.nome = getNome(descricao);
		this.vendedor = getVendedor(descricao);
		this.avaliacao = getAvaliacao(descricao);
		this.opinioes = getOpinioes(descricao);
		this.valorAVista = getValorAVista(valorProdudos);
		this.parcelas = getParcelas(parcelaProdutos);
		this.valorAPrazo = getValorAPrazo(parcelaProdutos);
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getValorProdudos() {
		return valorProdudos;
	}
	
	public void setValorProdudos(String valorProdudos) {
		this.valorProdudos = valorProdudos;
	}

	public String getParcelaProdutos() {
		return parcelaProdutos;
	}

	public void setParcelaProdutos(String parcelaProdutos) {
		this.parcelaProdutos = parcelaProdutos;
	}

	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNome(String descricao) {
		String[] descricoes = descricao.split("\n");
		return descricoes[0];
	}
	
	public String getVendedor() {
		return vendedor;
	}

	public void setVendedor(String vendedor) {
		this.vendedor = vendedor;
	}

	public String getVendedor(String descricao) {
		String[] descricoes = descricao.split("\n");
		
		if(descricoes.length-1 != 4) {
			return null;
		} else {
			return descricoes[1].replace("por", "").trim();
		}
	}

	public Double getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(Double avaliacao) {
		this.avaliacao = avaliacao;
	}
	
	public Double getAvaliacao(String descricao) {
		String[] descricoes = descricao.split("\n");
		if(descricoes.length-1 == 4) {
			return Double.valueOf(descricoes[3]);
		}
		if(descricoes.length-1 == 3) {
			return Double.valueOf(descricoes[2]);
		}
		return null;
	}
	
	public String getOpinioes() {
		return opinioes;
	}

	public void setOpinioes(String opinioes) {
		this.opinioes = opinioes;
	}

	public String getOpinioes(String descricao) {
		String[] descricoes = descricao.split("\n");
		Pattern pattern = Pattern.compile("\\b(\\d+)\\s+opiniões\\b");
		if(descricoes.length-1 == 4) {
			Matcher matcher = pattern.matcher(descricoes[2]);
			if (matcher.find()) {
				String numeroOpinioes = matcher.group(1);
	            return numeroOpinioes;
	        }
		}
		if(descricoes.length-1 == 3) {
		Matcher matcher = pattern.matcher(descricoes[1]);
		if (matcher.find()) {
			String numeroOpinioes = matcher.group(1);
            return numeroOpinioes;
        	}
		}
		return null;
	}
	
	public BigDecimal getValorAVista() {
		return valorAVista;
	}

	public void setValorAVista(BigDecimal valorAVista) {
		this.valorAVista = valorAVista;
	}

	public BigDecimal getValorAVista(String dadosValores) {
		return converterParaBigDecimal(dadosValores);
	}

	public String getParcelas() {
		return parcelas;
	}

	public void setParcelas(String parcelas) {
		this.parcelas = parcelas;
	}

	public String getParcelas(String parcelaProdutos) {
		return formatarTextoParcelas(parcelaProdutos);
	}

	public BigDecimal getValorAPrazo() {
		return valorAPrazo;
	}

	public void setValorAPrazo(BigDecimal valorAPrazo) {
		this.valorAPrazo = valorAPrazo;
	}

	public BigDecimal getValorAPrazo(String parcelaProdutos) {
		
		BigDecimal valorIsolado = extrairValor(parcelaProdutos);
	    int numeroParcelas = extrairNumeroParcelas(parcelaProdutos);

	    // Calculando o valor total
	    return valorIsolado.multiply(BigDecimal.valueOf(numeroParcelas));
	}
	
	private static BigDecimal converterParaBigDecimal(String input) {
        // Remover caracteres não numéricos e converter para BigDecimal
        try {
            NumberFormat format = NumberFormat.getInstance(new Locale("pt", "BR"));
            String cleanInput = input.replaceAll("[^\\d,]", "");
            return new BigDecimal(format.parse(cleanInput).toString());
        } catch (ParseException e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
	
	private static String formatarTextoParcelas(String input) {
		input = input.replace("\n", "");
        // Adicionar espaço antes de cada dígito que não está no início
        // e antes de "R$" apenas se não estiver no início
        input = input.replaceAll("(?<=\\D)(?=\\d)|(?<!^R\\$)(?=R\\$)", " ");
        // Adicionar espaço entre o valor e "sem juros"
        input = input.replaceAll("(?<=\\d)(?=sem juros)", " ");
        // Remover espaço entre vírgula e dígito
        input = input.replaceAll("(?<=,)\\s*(?=\\d)", "");
        // Remover espaço entre "R$" e o valor
        input = input.replaceAll("R\\$\\s*", Matcher.quoteReplacement("R$"));
        return input;
    }
	
	private static BigDecimal extrairValor(String input) {
		input = input.replace("\n", "");
        // Encontrar o padrão de valor usando expressão regular
        Pattern pattern = Pattern.compile("R\\$\\s*(\\d+)(?:,(\\d+))?"); // Modificado para aceitar valor sem casas decimais
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String parteInteira = matcher.group(1);
            String parteDecimal = matcher.group(2);
            String valorCompleto = parteDecimal != null ? parteInteira + "." + parteDecimal : parteInteira;
            return new BigDecimal(valorCompleto);
        } else {
            return BigDecimal.ZERO; // Retorna zero se o padrão não for encontrado
        }
    }
	
	private static int extrairNumeroParcelas(String input) {
		input = input.replace("\n", "");
        // Encontrar o número de parcelas usando expressão regular
        Pattern pattern = Pattern.compile("(\\d+)x");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return 1; // Retorna 1 se o padrão não for encontrado
        }
    }

	@Override
	public String toString() {
		return "nome=" + nome + ", vendedor="
				+ vendedor + ", avaliacao=" + avaliacao + ", opinioes=" + opinioes + ", valorAVista=" + valorAVista
				+ ", parcelas=" + parcelas + ", valorAPrazo=" + valorAPrazo + "]";
	}
	
}
