package edu.unisabana.tyvs.registry.application.usecase;

import edu.unisabana.tyvs.registry.application.port.out.RegistryRepositoryPort;
import edu.unisabana.tyvs.registry.domain.model.Gender;
import edu.unisabana.tyvs.registry.domain.model.Person;
import edu.unisabana.tyvs.registry.domain.model.RegisterResult;
import edu.unisabana.tyvs.registry.infrastructure.persistence.RegistryRepository;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * PRUEBA DE INTEGRACION: el caso de uso {@link Registry} contra una base de
 * datos H2 real (no un mock). Verifica que la persistencia realmente funciona.
 *
 * Por que el nombre termina en IT y no en Test:
 * en este taller *Test.java son pruebas UNITARIAS (las ejecuta Surefire en
 * "mvn test") y *IT.java son de INTEGRACION o sistema (las ejecuta Failsafe en
 * "mvn verify"). Esta clase toca una base de datos, asi que no es unitaria.
 * Compare con {@link RegistryWithMockTest}, que prueba la misma clase sin BD.
 *
 * Cada prueba usa su propia base (regdb_usecase_it) y limpia en el @Before:
 * H2 con DB_CLOSE_DELAY=-1 sobrevive mientras viva la JVM, y las JVM se
 * reutilizan entre clases de prueba.
 */
public class RegistryIT {

    private static final String JDBC_URL = "jdbc:h2:mem:regdb_usecase_it;DB_CLOSE_DELAY=-1";

    private RegistryRepositoryPort repo;
    private Registry registry;

    @Before
    public void setup() throws Exception {
        RegistryRepository repository = new RegistryRepository(JDBC_URL);
        repository.initSchema(); // Arrange: crear tabla
        repository.deleteAll(); // Arrange: estado limpio para cada prueba

        repo = repository;
        registry = new Registry(repo); // Arrange: inyectar dependencia
    }

    @Test
    public void shouldRegisterValidPerson() throws Exception {
        // Arrange
        Person p1 = new Person("Ana", 100, 30, Gender.FEMALE, true);

        // Act
        RegisterResult result = registry.registerVoter(p1);

        // Assert: el resultado Y el efecto real en la base de datos
        assertEquals(RegisterResult.VALID, result);
        assertTrue(repo.existsById(100));
    }

    @Test
    public void shouldPersistValidVoterAndRejectDuplicates() throws Exception {
        // Arrange
        Person p1 = new Person("Ana", 100, 30, Gender.FEMALE, true);
        Person p2 = new Person("AnaDos", 100, 40, Gender.FEMALE, true);

        // Act (primer registro)
        RegisterResult result1 = registry.registerVoter(p1);

        // Assert primer registro
        assertEquals(RegisterResult.VALID, result1);
        assertTrue(repo.existsById(100));

        // Act (segundo registro con el mismo id)
        RegisterResult result2 = registry.registerVoter(p2);

        // Assert: la unicidad la garantiza la base de datos, no el mock
        assertEquals(RegisterResult.DUPLICATED, result2);
    }

    /**
     * Caso de prueba: menor de edad.
     *
     * <p>A diferencia de {@link RegistryWithMockTest}, aqui no basta con
     * revisar el valor devuelto: hay que confirmar contra la BD real que
     * NINGUN registro quedo insertado, porque el caso de uso debe rechazar
     * al menor antes de tocar el repositorio.</p>
     */
    @Test
    public void shouldRejectUnderagePersonAndNotPersistIt() throws Exception {
        // Arrange
        Person menor = new Person("Sara", 200, 17, Gender.FEMALE, true);

        // Act
        RegisterResult result = registry.registerVoter(menor);

        // Assert: resultado de dominio Y ausencia real en la base de datos
        assertEquals(RegisterResult.UNDERAGE, result);
        assertFalse(repo.existsById(200));
    }

    /**
     * Caso de prueba: edad imposible (negativa).
     *
     * <p>Corresponde al Defecto 01 documentado en {@code defectos.md}: una
     * edad de -1 es un dato IMPOSIBLE, distinto de ser menor de edad. Se
     * verifica ademas que, igual que con UNDERAGE, no se persiste nada.</p>
     */
    @Test
    public void shouldRejectImpossibleAgeAndNotPersistIt() throws Exception {
        // Arrange
        Person edadImposible = new Person("Imposible", 201, -1, Gender.UNIDENTIFIED, true);

        // Act
        RegisterResult result = registry.registerVoter(edadImposible);

        // Assert
        assertEquals(RegisterResult.INVALID_AGE, result);
        assertFalse(repo.existsById(201));
    }

    /**
     * Caso de prueba: edad por encima del maximo biologico (121).
     *
     * <p>Misma clase de equivalencia que la edad negativa (INVALID_AGE), pero
     * en el extremo superior. Se prueba por separado porque ambos bordes de
     * la frontera deben validarse de forma independiente.</p>
     */
    @Test
    public void shouldRejectAgeAboveBiologicalMaximum() throws Exception {
        // Arrange
        Person edadImposible = new Person("Matusalen", 202, 121, Gender.MALE, true);

        // Act
        RegisterResult result = registry.registerVoter(edadImposible);

        // Assert
        assertEquals(RegisterResult.INVALID_AGE, result);
        assertFalse(repo.existsById(202));
    }

    /**
     * Caso de prueba: persona fallecida.
     *
     * <p>Verifica que {@code alive=false} se rechaza con BD real de por
     * medio, y que ese rechazo ocurre ANTES de cualquier intento de
     * persistencia (no queda registro en la tabla).</p>
     */
    @Test
    public void shouldRejectDeadPersonAndNotPersistIt() throws Exception {
        // Arrange
        Person fallecido = new Person("Pedro", 203, 50, Gender.MALE, false);

        // Act
        RegisterResult result = registry.registerVoter(fallecido);

        // Assert
        assertEquals(RegisterResult.DEAD, result);
        assertFalse(repo.existsById(203));
    }

    /**
     * Caso de prueba: identificador invalido (id <= 0).
     *
     * <p>Completa las clases de equivalencia de entrada invalida exigidas
     * por el taller. Un id de 0 o negativo no corresponde a ningun
     * documento real, y el caso de uso debe rechazarlo sin consultar la
     * base de datos.</p>
     */
    @Test
    public void shouldRejectNonPositiveIdAndNotPersistIt() throws Exception {
        // Arrange
        Person idInvalido = new Person("Nadie", 0, 30, Gender.UNIDENTIFIED, true);

        // Act
        RegisterResult result = registry.registerVoter(idInvalido);

        // Assert
        assertEquals(RegisterResult.INVALID, result);
        assertFalse(repo.existsById(0));
    }
}
