package splitter;

import splitter.annotations.AggregateRoot;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static splitter.rules.HaveExactlyOneAggregateRoot.HAVE_EXACTLY_ONE_AGGREGATE_ROOT;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = SplitterApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchUnitTest {

   @ArchTest
    ArchRule onionTest = onionArchitecture()
            .domainModels("..domain..")
            .domainServices("..service..")
            .applicationServices("..service..")
            .adapter("web","..web..")
            .adapter("api","..api..")
            .adapter("db", "..repositories..");

    @ArchTest
    static final ArchRule onlyAggregateRootsArePublic = classes()
            .that()
            .areNotAnnotatedWith(AggregateRoot.class)
            .and()
            .resideInAPackage("..domain..")
            .should()
            .notBePublic()
            .because("the implementation of an aggregate " +
                    "should be hidden");

    @ArchTest
    static final ArchRule oneAggregateRootPerAggregate = slices()
            .matching("..domain.(*)..")
            .should(HAVE_EXACTLY_ONE_AGGREGATE_ROOT);
}
