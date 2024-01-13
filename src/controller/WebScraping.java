 package controller;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;



import model.Produto;

public class WebScraping {
	
	public static void main(String[] args) {
		criarPlanilha(rasparDados());
	}
	
	public static ArrayList<Produto> rasparDados() {
		System.setProperty("webdriver.edge.driver", "resources/msedgedriver.exe");
		
		EdgeOptions options = new EdgeOptions();
		
		// Corrigir possíveis falhas em execução
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");
		
		// Evitar identificação como bot
		options.addArguments("--disable-blink-features=AutomationControlled");
		options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
		options.setExperimentalOption("useAutomationExtension", null);
		options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
		
		options.addArguments("window-size=1600,800");
		
		WebDriver driver = new EdgeDriver(options);
		
		driver.get("https://mercadolivre.com.br");
		
		WebElement inputPesquisa = driver.findElement(By.xpath("//input[@id=\"cb1-edit\"]"));
		
		inputPesquisa.sendKeys("pc gamer");
		
		inputPesquisa.submit();
		
		waitForPageLoad(5000);
		
		// Dados dos produtos
		List<WebElement> descricaoProdutos = driver.findElements(By.xpath("//*[@class=\"ui-search-item__group ui-search-item__group--title\"]"));
		List<WebElement> valorProdudos = driver.findElements(By.xpath("//*[@class=\"andes-money-amount ui-search-price__part ui-search-price__part--medium andes-money-amount--cents-superscript\"]"));
		List<WebElement> parcelaProdutos = driver.findElements(By.xpath("//*[@class=\"ui-search-item__group__element ui-search-installments ui-search-color--LIGHT_GREEN\" or @class=\"ui-search-item__group__element ui-search-installments ui-search-color--BLACK\"]"));
		
		ArrayList<Produto> produtos = new ArrayList<>();
		
		for(int i=0; i<descricaoProdutos.size(); i++) {
			try {
			produtos.add(new Produto(descricaoProdutos.get(i).getText(), valorProdudos.get(i).getText(), parcelaProdutos.get(i).getText()));
			} catch(IndexOutOfBoundsException e) {
				
			}
		}
		
		waitForPageLoad(5000);
		
		driver.quit();
		
		return produtos;
	}
	
	private static void criarPlanilha(ArrayList<Produto> produtos){
		//cria um arquivo excel
		Workbook pastaTrabalho = new XSSFWorkbook();
		
		//cria a planilha
		Sheet planilha = pastaTrabalho.createSheet("PRODUTOS");
		
		//cria fonte no estilo negrito
		Font fonteNegrito = pastaTrabalho.createFont();
		fonteNegrito.setBold(true);
		
		//definir celulas negrito
		CellStyle estiloNegrito = pastaTrabalho.createCellStyle();
		estiloNegrito.setFont(fonteNegrito);
		
		//cria as colunas
		Row linha = planilha.createRow(0);
		Cell celula1 = linha.createCell(0);
		celula1.setCellValue("Nome");
		celula1.setCellStyle(estiloNegrito);
		
		Cell celula2 = linha.createCell(1);
		celula2.setCellValue("Vendendor");
		celula2.setCellStyle(estiloNegrito);
		
		Cell celula3 = linha.createCell(2);
		celula3.setCellValue("Avaliação");
		celula3.setCellStyle(estiloNegrito);
		
		Cell celula4 = linha.createCell(3);
		celula4.setCellValue("Opiniões");
		celula4.setCellStyle(estiloNegrito);
		
		Cell celula5 = linha.createCell(4);
		celula5.setCellValue("Valor a vista");
		celula5.setCellStyle(estiloNegrito);
		
		Cell celula6 = linha.createCell(5);
		celula6.setCellValue("Qtd. parcelas");
		celula6.setCellStyle(estiloNegrito);
		
		Cell celula7 = linha.createCell(6);
		celula7.setCellValue("Valor a prazo");
		celula7.setCellStyle(estiloNegrito);
		
		planilha.autoSizeColumn(0);
		planilha.autoSizeColumn(1);
		planilha.autoSizeColumn(2);
		planilha.autoSizeColumn(3);
		planilha.autoSizeColumn(4);
		planilha.autoSizeColumn(5);
		planilha.autoSizeColumn(6);
		
		
		if(produtos.size() > 0) {
			
			int i = 1;
			for(Produto produto: produtos) {
				Row linhaProduto = planilha.createRow(i);
				Cell celulaNome = linhaProduto.createCell(0);
				celulaNome.setCellValue(produto.getNome());
				
				Cell celulaVendedor = linhaProduto.createCell(1);
				celulaVendedor.setCellValue(produto.getVendedor() == null ? "" : produto.getVendedor());
				
				Cell celulaAvaliacao = linhaProduto.createCell(2);
				celulaAvaliacao.setCellValue(produto.getAvaliacao() == null ? "" : produto.getAvaliacao().toString());
				
				Cell celulaOpinioes = linhaProduto.createCell(3);
				celulaOpinioes.setCellValue(produto.getOpinioes() == null ? "" : produto.getOpinioes());
				
				Cell celulaValorAvista = linhaProduto.createCell(4);
				celulaValorAvista.setCellValue(produto.getValorAVista().toString());
				
				Cell celulaQuantidadeParcelas = linhaProduto.createCell(5);
				celulaQuantidadeParcelas.setCellValue(produto.getParcelas());
				
				Cell celulaValorAPrazo = linhaProduto.createCell(6);
				celulaValorAPrazo.setCellValue(produto.getValorAPrazo().toString());
				i++;
			}
		}
		
		try (FileOutputStream arquivo = new FileOutputStream("produtos.xlsx")){
			pastaTrabalho.write(arquivo);
			
			JOptionPane.showMessageDialog(null, "Planilha criada com sucesso");
		}catch (Exception e) {
			System.out.println("Erro ao criar a planilha: " + e.getMessage());
		}finally {
			try {
				pastaTrabalho.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// Espera a página ser carregada
	private static void waitForPageLoad(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
