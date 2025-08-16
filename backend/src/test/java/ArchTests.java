import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import io.manurasahs.deltavault.DeltaVaultApplication;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(
    packagesOf = DeltaVaultApplication.class,
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class ArchTests
{

    @ArchTest
    public static void checkArchitectureLayersAccessibility(JavaClasses classes)
    {
        layeredArchitecture()
            .consideringAllDependencies()
            .layer("domain").definedBy("..deltavault.domain..")
            .layer("application").definedBy("..deltavault.application..")
            .layer("port.adapter").definedBy("..port.adapter..")
            .layer("configuration").definedBy("..deltavault.configuration..")
            .whereLayer("domain").mayOnlyBeAccessedByLayers("application", "port.adapter")
            .whereLayer("application").mayOnlyBeAccessedByLayers("port.adapter")
            .whereLayer("port.adapter").mayNotBeAccessedByAnyLayer()
            .whereLayer("configuration").mayNotBeAccessedByAnyLayer()
            .check(classes);
    }

    @ArchTest
    public static void adaptersShouldBeIndependentOneFromAnother(JavaClasses classes)
    {
        layeredArchitecture()
            .consideringAllDependencies()
            .layer("port.adapter.awss3").definedBy("..port.adapter.awss3..")
            .layer("port.adapter.clientrest").definedBy("..port.adapter.clientrest..")
            .layer("port.adapter.dynamo").definedBy("..port.adapter.dynamo..")
            .whereLayer("port.adapter.awss3").mayNotBeAccessedByAnyLayer()
            .whereLayer("port.adapter.clientrest").mayNotBeAccessedByAnyLayer()
            .whereLayer("port.adapter.dynamo").mayNotBeAccessedByAnyLayer()
            .check(classes);
    }

    @ArchTest
    public static void controllersMustResideInAdapterSubPackages(JavaClasses classes)
    {
        classes().that()
            .areAnnotatedWith(RestController.class)
            .should()
            .resideInAPackage("..port.adapter..")
            .check(classes);
    }

    //    @ArchTest
    public static void repositoriesMustResideInProperAdapters(JavaClasses classes)
    {
        classes().that()
            .areAnnotatedWith(Repository.class)
            .should()
            .resideInAnyPackage("..port.adapter.dynamo..", "..port.adapter.awss3..")
            .check(classes);
    }
}