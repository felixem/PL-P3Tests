import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Permission;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import junit.framework.TestCase;

@RunWith(Parameterized.class)
public class TestsTraductorDR extends TestCase {
	// Excepción de salida
	protected static class ExitException extends SecurityException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public final int status;

		public ExitException(int status) {
			super("There is no escape!");
			this.status = status;
		}
	}

	// Security Manager preparado para evitar la salida
	private static class NoExitSecurityManager extends SecurityManager {
		@Override
		public void checkPermission(Permission perm) {
			// allow anything.
		}

		@Override
		public void checkPermission(Permission perm, Object context) {
			// allow anything.
		}

		@Override
		public void checkExit(int status) {
			super.checkExit(status);
			throw new ExitException(status);
		}
	}

	// Buffers para usar en entrada-salida
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		// Redirigir buffers de entrada-salida
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
		// Cambiar gestor de seguridad
		System.setSecurityManager(new NoExitSecurityManager());
	}

	@Override
	@After
	public void tearDown() throws Exception {
		// Cambiar sistema de seguridad
		System.setSecurityManager(null); // or save and restore original
		// Limpiar buffers
		System.setOut(null);
		System.setErr(null);
		super.tearDown();
	}

	@Parameters(name = "{index}: fuente:{0}")
	// Parámetros de los distintos tests
	public static Collection<Object[]> data() {
		return Arrays
				.asList(new Object[][] {
						// Pruebas del profesor
						{ "resources/fuentes/p01.txt", "", "Error lexico (1,5): caracter '.' incorrecto", -1 },
						{ "resources/fuentes/p02.txt", "",
								"Error sintactico (6,3): encontrado 'b', esperaba * / + - ; ", -1 },
				{ "resources/fuentes/p03.txt", "resources/salidas/p03.sal", "", 0 },
				{ "resources/fuentes/p04.txt", "", "Error semantico (15,7): 'c' no ha sido declarado", -1 },
				{ "resources/fuentes/p05.txt", "resources/salidas/p05.sal", "", 0 },
				{ "resources/fuentes/p06.txt", "resources/salidas/p06.sal", "", 0 },
				{ "resources/fuentes/p07.txt", "", "Error lexico (5,6): caracter '.' incorrecto", -1 },
				{ "resources/fuentes/p08.txt", "", "Error lexico (5,11): caracter ':' incorrecto", -1 },
				{ "resources/fuentes/p09.txt", "", "Error lexico: fin de fichero inesperado", -1 },
				{ "resources/fuentes/p10.txt", "", "Error sintactico (1,6): encontrado 'main', esperaba ( ", -1 },
				// Pruebas adicionales del profesor
				{ "resources/fuentes/p11.txt", "",
						"Error sintactico (5,1): encontrado 'double', esperaba fin de fichero ", -1 },
				{ "resources/fuentes/p12.txt", "",
						"Error sintactico: encontrado fin de fichero, esperaba numero entero identificador numero real ",
						-1 },
				{ "resources/fuentes/p13.txt", "", "Error sintactico (1,5): encontrado 'main', esperaba identificador ",
						-1 },
				{ "resources/fuentes/p14.txt", "", "Error semantico (5,11): 'funcion' no es una variable", -1 },
				{ "resources/fuentes/p15.txt", "", "Error semantico (8,3): 'a' debe ser de tipo real", -1 },
				{ "resources/fuentes/p16.txt", "resources/salidas/p16.sal", "", 0 },
				{ "resources/fuentes/p17.txt", "", "Error semantico (12,9): '0' debe ser mayor que cero", -1 },
				{ "resources/fuentes/p18.txt", "", "Error sintactico (4,35): encontrado ';', esperaba ] ", -1 },
				{ "resources/fuentes/p19.txt", "", "Error sintactico (4,17): encontrado ';', esperaba identificador ",
						-1 },
				{ "resources/fuentes/p20.txt", "", "Error sintactico (7,3): encontrado 'a', esperaba ; , [ ", -1 },
				// Pruebas propias
				{ "resources/fuentes/felixem/comentarios01.txt", "resources/salidas/felixem/comentarios01.sal", "", 0 },
				{ "resources/fuentes/felixem/comentarios02.txt", "resources/salidas/felixem/comentarios02.sal", "", 0 },
				{ "resources/fuentes/felixem/vacio.txt", "",
						"Error sintactico: encontrado fin de fichero, esperaba 'double' 'int' 'main' ", -1 },
				{ "resources/fuentes/felixem/main01.txt", "",
						"Error sintactico (1,1): encontrado 'long', esperaba 'double' 'int' 'main' ", -1 },
				{ "resources/fuentes/felixem/main02.txt", "", "Error sintactico (2,1): encontrado '{', esperaba ( ",
						-1 },
				{ "resources/fuentes/felixem/main03.txt", "", "Error sintactico (1,5): encontrado ')', esperaba ( ",
						-1 },
				{ "resources/fuentes/felixem/main04.txt", "",
						"Error sintactico: encontrado fin de fichero, esperaba { ", -1 },
				{ "resources/fuentes/felixem/main05.txt", "",
						"Error sintactico: encontrado fin de fichero, esperaba { } 'double' 'int' identificador ", -1 },
				{ "resources/fuentes/felixem/main06.txt", "",
						"Error sintactico (3,6): encontrado 'double', esperaba identificador ", -1 },
				{ "resources/fuentes/felixem/main07.txt", "", "Error sintactico (3,9): encontrado '(', esperaba ; , [ ",
						-1 },
				{ "resources/fuentes/felixem/main08.txt", "",
						"Error sintactico (3,10): encontrado ']', esperaba numero entero ", -1 },
				{ "resources/fuentes/felixem/main09.txt", "",
						"Error sintactico (3,10): encontrado '1.3', esperaba numero entero ", -1 },
				{ "resources/fuentes/felixem/main10.txt", "", "Error sintactico (3,12): encontrado ';', esperaba ] ",
						-1 },
				{ "resources/fuentes/felixem/main11.txt", "",
						"Error sintactico (3,13): encontrado 'a', esperaba numero entero ", -1 },
				{ "resources/fuentes/felixem/main12.txt", "",
						"Error sintactico (3,10): encontrado 'double', esperaba identificador ", -1 },
				{ "resources/fuentes/felixem/main13.txt", "",
						"Error sintactico (4,2): encontrado '1.3', esperaba { } 'double' 'int' identificador ", -1 },
				{ "resources/fuentes/felixem/main14.txt", "",
						"Error sintactico (4,2): encontrado '[', esperaba { } 'double' 'int' identificador ", -1 },
				{ "resources/fuentes/felixem/main15.txt", "", "Error sintactico (4,5): encontrado ';', esperaba = ",
						-1 },
				{ "resources/fuentes/felixem/main16.txt", "",
						"Error sintactico (4,8): encontrado ';', esperaba numero entero identificador numero real ",
						-1 },
				{ "resources/fuentes/felixem/main17.txt", "",
						"Error sintactico (4,9): encontrado '(', esperaba * / + - ; ", -1 },
				{ "resources/fuentes/felixem/main18.txt", "",
						"Error sintactico (4,12): encontrado ';', esperaba numero entero identificador numero real ",
						-1 },
				{ "resources/fuentes/felixem/main19.txt", "",
						"Error sintactico (4,12): encontrado ';', esperaba numero entero identificador numero real ",
						-1 },
				{ "resources/fuentes/felixem/main20.txt", "",
						"Error sintactico (4,13): encontrado '(', esperaba * / + - ; ", -1 },
				{ "resources/fuentes/felixem/main21.txt", "",
						"Error sintactico (4,11): encontrado '(', esperaba * / + - ; ", -1 },
				{ "resources/fuentes/felixem/main22.txt", "",
						"Error sintactico (4,9): encontrado '(', esperaba * / + - ; ", -1 },
				{ "resources/fuentes/felixem/main23.txt", "",
						"Error sintactico (6,6): encontrado ';', esperaba identificador ", -1 },
				{ "resources/fuentes/felixem/main24.txt", "", "Error sintactico (3,8): encontrado '=', esperaba ; , [ ",
						-1 },
				{ "resources/fuentes/felixem/main25.txt", "",
						"Error sintactico (3,14): encontrado 'a', esperaba numero entero ", -1 },
				{ "resources/fuentes/felixem/main26.txt", "",
						"Error sintactico: encontrado fin de fichero, esperaba { } 'double' 'int' identificador ", -1 },
				{ "resources/fuentes/felixem/main27.txt", "", "Error sintactico (4,5): encontrado '[', esperaba = ",
						-1 },
				{ "resources/fuentes/felixem/main28.txt", "",
						"Error sintactico (3,2): encontrado 'main', esperaba { } 'double' 'int' identificador ", -1 },
				{ "resources/fuentes/felixem/main29.txt", "",
						"Error sintactico (3,6): encontrado 'main', esperaba identificador ", -1 },
				{ "resources/fuentes/felixem/main30.txt", "",
						"Error sintactico (4,8): encontrado 'main', esperaba numero entero identificador numero real ",
						-1 },
				{ "resources/fuentes/felixem/funcion01.txt", "",
						"Error sintactico (1,5): encontrado '33', esperaba identificador ", -1 },
				{ "resources/fuentes/felixem/funcion02.txt", "", "Error sintactico (1,12): encontrado ';', esperaba ( ",
						-1 },
				{ "resources/fuentes/felixem/funcion03.txt", "", "Error sintactico (1,13): encontrado ';', esperaba ) ",
						-1 },
				{ "resources/fuentes/felixem/funcion04.txt", "",
						"Error sintactico: encontrado fin de fichero, esperaba { } 'double' 'int' identificador ", -1 },
				{ "resources/fuentes/felixem/funcion05.txt", "",
						"Error sintactico (5,1): encontrado 'funcion', esperaba 'double' 'int' 'main' ", -1 },
				{ "resources/fuentes/felixem/funcion06.txt", "",
						"Error sintactico: encontrado fin de fichero, esperaba 'double' 'int' 'main' ", -1 },
				{ "resources/fuentes/felixem/funcion07.txt", "",
						"Error sintactico (1,5): encontrado 'main', esperaba identificador ", -1 },
				{ "resources/fuentes/felixem/funcion08.txt", "",
						"Error sintactico (1,13): encontrado 'int', esperaba ) ", -1 },
				{ "resources/fuentes/felixem/funcion09.txt", "",
						"Error sintactico (5,1): encontrado 'int', esperaba fin de fichero ", -1 },
				{ "resources/fuentes/pacocr/main01.txt", "",
						"Error sintactico (1,1): encontrado 'ma', esperaba 'double' 'int' 'main' ", -1 },
				{ "resources/fuentes/pacocr/main02.txt", "", "Error sintactico: encontrado fin de fichero, esperaba ( ",
						-1 },
				{ "resources/fuentes/felixem/semantico01.txt", "", "Error semantico (4,2): 'var' no es una variable",
						-1 },
				{ "resources/fuentes/felixem/semantico02.txt", "", "Error semantico (4,2): 'var' no es una variable",
						-1 },
				{ "resources/fuentes/felixem/semantico03.txt", "", "Error semantico (13,3): 'val' no es una variable",
						-1 },
				{ "resources/fuentes/felixem/semantico04.txt", "",
						"Error semantico (11,2): 'funcion1' no es una variable", -1 },
				{ "resources/fuentes/felixem/semantico05.txt", "", "Error semantico (12,9): 'var2' no es una variable",
						-1 },
				{ "resources/fuentes/felixem/semantico06.txt", "",
						"Error semantico (12,9): 'funcion1' no es una variable", -1 },
				{ "resources/fuentes/felixem/semantico07.txt", "",
						"Error semantico (12,9): 'funcion1' no es una variable", -1 },
				{ "resources/fuentes/felixem/semantico08.txt", "", "Error semantico (12,9): 'var2' no es una variable",
						-1 },
				{ "resources/fuentes/felixem/semantico09.txt", "",
						"Error semantico (12,13): 'funcion1' no es una variable", -1 },
				{ "resources/fuentes/felixem/semantico10.txt", "", "Error semantico (12,13): 'var2' no es una variable",
						-1 },
				{ "resources/fuentes/felixem/semantico11.txt", "", "Error semantico (12,13): 'var2' no es una variable",
						-1 },
				{ "resources/fuentes/felixem/semantico12.txt", "",
						"Error semantico (12,13): 'funcion1' no es una variable", -1 },
				{ "resources/fuentes/felixem/semantico13.txt", "",
						"Error semantico (9,5): 'funcion1' ya existe en este ambito", -1 },
				{ "resources/fuentes/felixem/semantico14.txt", "",
						"Error semantico (5,9): 'var' ya existe en este ambito", -1 },
				{ "resources/fuentes/felixem/semantico15.txt", "",
						"Error semantico (4,11): 'var' ya existe en este ambito", -1 },
				{ "resources/fuentes/felixem/semantico16.txt", "",
						"Error semantico (8,7): 'var2' ya existe en este ambito", -1 },
				{ "resources/fuentes/felixem/semantico17.txt", "",
						"Error semantico (7,13): 'var1' ya existe en este ambito", -1 },
				{ "resources/fuentes/felixem/semantico18.txt", "",
						"Error semantico (4,10): '0' debe ser mayor que cero", -1 },
				{ "resources/fuentes/felixem/semantico19.txt", "",
						"Error semantico (4,13): '0' debe ser mayor que cero", -1 },
				{ "resources/fuentes/felixem/semantico20.txt", "",
						"Error semantico (4,19): '0' debe ser mayor que cero", -1 },
				{ "resources/fuentes/felixem/semantico21.txt", "", "Error semantico (4,2): 'var1' no ha sido declarado",
						-1 },
				{ "resources/fuentes/felixem/semantico22.txt", "", "Error semantico (5,9): 'var' no ha sido declarado",
						-1 },
				{ "resources/fuentes/felixem/semantico23.txt", "", "Error semantico (5,13): 'var' no ha sido declarado",
						-1 },
				{ "resources/fuentes/felixem/semantico24.txt", "", "Error semantico (5,13): 'var' no ha sido declarado",
						-1 },
				{ "resources/fuentes/felixem/semantico25.txt", "", "Error semantico (8,2): 'var1' no ha sido declarado",
						-1 },
				{ "resources/fuentes/felixem/semantico26.txt", "", "Error semantico (9,3): 'var1' no ha sido declarado",
						-1 },
				{ "resources/fuentes/felixem/semantico27.txt", "",
						"Error semantico (10,2): 'var1' no ha sido declarado", -1 },
				{ "resources/fuentes/felixem/semantico28.txt", "",
						"Error semantico (10,2): 'var1' no ha sido declarado", -1 },
				{ "resources/fuentes/felixem/semantico29.txt", "",
						"Error semantico (12,2): 'var1' no ha sido declarado", -1 },
				{ "resources/fuentes/felixem/semantico30.txt", "",
						"Error semantico (13,3): 'var1' no ha sido declarado", -1 },
				{ "resources/fuentes/felixem/semantico31.txt", "", "Error semantico (5,2): 'var' debe ser de tipo real",
						-1 },
				{ "resources/fuentes/felixem/semantico32.txt", "", "Error semantico (5,2): 'var' debe ser de tipo real",
						-1 },
				{ "resources/fuentes/felixem/semantico33.txt", "", "Error semantico (5,2): 'var' debe ser de tipo real",
						-1 },
				{ "resources/fuentes/felixem/semantico34.txt", "", "Error semantico (6,2): 'var' debe ser de tipo real",
						-1 },
				{ "resources/fuentes/felixem/semantico35.txt", "", "Error semantico (5,2): 'var' debe ser de tipo real",
						-1 },
				{ "resources/fuentes/felixem/semantico36.txt", "", "Error semantico (5,2): 'var' debe ser de tipo real",
						-1 },
				{ "resources/fuentes/felixem/semantico37.txt", "", "Error semantico (5,2): 'var' debe ser de tipo real",
						-1 },
				{ "resources/fuentes/felixem/semantico38.txt", "", "Error semantico (6,2): 'var' debe ser de tipo real",
						-1 },
				{ "resources/fuentes/felixem/semantico39.txt", "", "Error semantico (6,2): 'var' debe ser de tipo real",
						-1 },
				{ "resources/fuentes/felixem/semantico40.txt", "", "Error semantico (6,2): 'var' debe ser de tipo real",
						-1 },
				{ "resources/fuentes/felixem/semantico41.txt", "", "Error semantico (6,2): 'var' debe ser de tipo real",
						-1 },
				{ "resources/fuentes/felixem/semantico42.txt", "", "Error semantico (6,2): 'var' debe ser de tipo real",
						-1 },
				{ "resources/fuentes/felixem/semantico43.txt", "", "Error semantico (6,2): 'var' debe ser de tipo real",
						-1 },
				{ "resources/fuentes/felixem/semantico44.txt", "", "Error semantico (6,2): 'var' debe ser de tipo real",
						-1 },
				{ "resources/fuentes/felixem/traduccion01.txt", "resources/salidas/felixem/traduccion01.sal", "",
						0 }, 
				{ "resources/fuentes/felixem/traduccion02.txt", "resources/salidas/felixem/traduccion02.sal", "",
						0 }, 
				});
	}

	// Leer fichero y volcarlo en String
	private void readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		this.salidaEsperada = new String(encoded, encoding);
	}

	// Atributos del test
	private String fichero;
	private String salidaEsperada;
	private String errorEsperado;
	private int codigoErrorEsperado;

	// Constructor por parámetros
	public TestsTraductorDR(String fichero, String ficSalida, String errorEsperado, int codigoErrorEsperado)
			throws IOException {
		super();
		this.fichero = fichero;
		this.errorEsperado = errorEsperado;
		this.codigoErrorEsperado = codigoErrorEsperado;

		// Leer fichero
		if (!ficSalida.isEmpty()) {
			this.readFile(ficSalida, Charset.forName("UTF-8"));
		} else {
			this.salidaEsperada = "";
		}
	}

	// Comparar dos listas de tokens
	public void compareTokensList(List<Token> expectedTokens, List<Token> resultTokens) {
		Assert.assertEquals("Cantidad de tokens", expectedTokens.size(), resultTokens.size());
		// Comparar token a token
		for (int i = 0; i < expectedTokens.size(); ++i) {
			Token expected = expectedTokens.get(i);
			Token result = resultTokens.get(i);
			// Comparar atributos
			Assert.assertEquals("Token " + i + " Fila", expected.fila, result.fila);
			Assert.assertEquals("Token " + i + " Columna", expected.columna, result.columna);
			Assert.assertEquals("Token " + i + " Lexema", expected.lexema, result.lexema);
			Assert.assertEquals("Token " + i + " Tipo", expected.tipo, result.tipo);
		}
	}

	// Tests lexicos del profesor
	@Test
	public void test() throws Exception {
		try {
			plp3.main(new String[] { fichero });
		} catch (ExitException e) {
			// Comparar salida esperada y error esperado
			Assert.assertEquals("Código de error esperado", codigoErrorEsperado, e.status);
			Assert.assertEquals("Salida esperada", salidaEsperada, outContent.toString());
			Assert.assertEquals("Error esperado", errorEsperado, errContent.toString());
			return;
		}

		// Comprobar si se esperaba error
		if (codigoErrorEsperado != 0)
			Assert.fail("Se esperaba que el programa saliera con código de error " + codigoErrorEsperado);

		Assert.assertEquals("Salida esperada", salidaEsperada, outContent.toString());
		Assert.assertEquals("Error esperado", errorEsperado, errContent.toString());
	}

}
