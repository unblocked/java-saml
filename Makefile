# Java SAML Toolkit Makefile
# Provides common commands for building and managing the project

# Variables
MAVEN_OPTS ?= -Xmx1024m
LOCAL_REPO = .maven
SKIP_TESTS ?= false
# SKIP_DEPENDENCY_CHECK removed - dependency check plugin has been removed
DIST_DIR = dist
VERSION = $(shell mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

# Default target
.PHONY: help
help: ## Show this help message
	@echo "Java SAML Toolkit - Available Commands:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'
	@echo ""
	@echo "Environment Variables:"
	@echo "  MAVEN_OPTS           Maven JVM options (default: -Xmx1024m)"
	@echo "  SKIP_TESTS           Skip running tests (default: false)"

# Clean targets
.PHONY: clean
clean: ## Clean all build artifacts
	mvn clean

.PHONY: clean-local-repo
clean-local-repo: ## Clean the local Maven repository
	rm -rf $(LOCAL_REPO)

.PHONY: clean-all
clean-all: clean clean-local-repo clean-dist ## Clean everything including local repository and distribution

.PHONY: clean-dist
clean-dist: ## Clean distribution directory
	rm -rf $(DIST_DIR)

# Build targets
.PHONY: compile
compile: ## Compile the project
	mvn compile

.PHONY: test
test: ## Run all tests
	mvn test

.PHONY: test-skip
test-skip: ## Compile and package without running tests
	mvn package -DskipTests=true

.PHONY: package
package: ## Package the project (includes tests)
ifeq ($(SKIP_TESTS),true)
	mvn package -DskipTests=true
else
	mvn package
endif

# Install targets
.PHONY: install
install: ## Install to default Maven local repository (~/.m2/repository)
ifeq ($(SKIP_TESTS),true)
	mvn install -DskipTests=true
else
	mvn install
endif

.PHONY: install-local
install-local: ## Install to local repository (.maven directory)
ifeq ($(SKIP_TESTS),true)
	mvn install -DskipTests=true -Dmaven.repo.local=$(LOCAL_REPO)
else
	mvn install -Dmaven.repo.local=$(LOCAL_REPO)
endif

.PHONY: install-local-clean
install-local-clean: clean-local-repo install-local ## Clean local repo and install fresh

.PHONY: install-local-minimal
install-local-minimal: ## Create Gradle-compatible Maven repository with project artifacts
	@echo "Creating Gradle-compatible Maven repository..."
	@rm -rf $(LOCAL_REPO)
	@mkdir -p $(LOCAL_REPO)

	# Build project artifacts (including adapters)
	mvn clean package -DskipTests=$(SKIP_TESTS)
	cd adapters && mvn clean package -DskipTests=$(SKIP_TESTS)

	# Install parent POM first (required for child POMs)
	mvn install:install-file -Dfile=pom.xml \
		-DgroupId=com.onelogin -DartifactId=java-saml-toolkit -Dversion=$(VERSION) \
		-Dpackaging=pom -DlocalRepositoryPath=$(LOCAL_REPO) -DcreateChecksum=true

	mvn install:install-file -Dfile=core/target/java-saml-core-$(VERSION).jar \
		-DgroupId=com.onelogin -DartifactId=java-saml-core -Dversion=$(VERSION) \
		-Dpackaging=jar -DlocalRepositoryPath=$(LOCAL_REPO) -DcreateChecksum=true

	mvn install:install-file -Dfile=toolkit/target/java-saml-$(VERSION).jar \
		-DgroupId=com.onelogin -DartifactId=java-saml -Dversion=$(VERSION) \
		-Dpackaging=jar -DlocalRepositoryPath=$(LOCAL_REPO) -DcreateChecksum=true

	mvn install:install-file -Dfile=samples/java-saml-tookit-jspsample/target/java-saml-tookit-jspsample-$(VERSION).war \
		-DgroupId=com.onelogin -DartifactId=java-saml-tookit-jspsample -Dversion=$(VERSION) \
		-Dpackaging=war -DlocalRepositoryPath=$(LOCAL_REPO) -DcreateChecksum=true

	# Install adapters parent POM (required for adapter modules)
	mvn install:install-file -Dfile=adapters/pom.xml \
		-DgroupId=com.onelogin -DartifactId=java-saml-adapters -Dversion=$(VERSION) \
		-Dpackaging=pom -DlocalRepositoryPath=$(LOCAL_REPO) -DcreateChecksum=true

	# Install adapter artifacts
	mvn install:install-file -Dfile=adapters/java-saml-jetty/target/java-saml-jetty-$(VERSION).jar \
		-DgroupId=com.onelogin -DartifactId=java-saml-jetty -Dversion=$(VERSION) \
		-Dpackaging=jar -DlocalRepositoryPath=$(LOCAL_REPO) -DcreateChecksum=true

	# Rename maven-metadata-local.xml to maven-metadata.xml for Gradle compatibility
	@find $(LOCAL_REPO) -name "maven-metadata-local.xml" -exec sh -c 'mv "$$1" "$${1%-local.xml}.xml"' _ {} \;

	@echo "Gradle-compatible Maven repository created:"
	@find $(LOCAL_REPO) -type f | sort

.PHONY: generate-maven-metadata
generate-maven-metadata: ## Generate Maven metadata files for local repository
	@echo "Generating Maven metadata files..."

	# Core metadata
	@echo '<?xml version="1.0" encoding="UTF-8"?>' > $(LOCAL_REPO)/com/onelogin/java-saml-core/maven-metadata-local.xml
	@echo '<metadata>' >> $(LOCAL_REPO)/com/onelogin/java-saml-core/maven-metadata-local.xml
	@echo '  <groupId>com.onelogin</groupId>' >> $(LOCAL_REPO)/com/onelogin/java-saml-core/maven-metadata-local.xml
	@echo '  <artifactId>java-saml-core</artifactId>' >> $(LOCAL_REPO)/com/onelogin/java-saml-core/maven-metadata-local.xml
	@echo '  <versioning>' >> $(LOCAL_REPO)/com/onelogin/java-saml-core/maven-metadata-local.xml
	@echo '    <release>$(VERSION)</release>' >> $(LOCAL_REPO)/com/onelogin/java-saml-core/maven-metadata-local.xml
	@echo '    <versions><version>$(VERSION)</version></versions>' >> $(LOCAL_REPO)/com/onelogin/java-saml-core/maven-metadata-local.xml
	@echo '    <lastUpdated>'$$(date +%Y%m%d%H%M%S)'</lastUpdated>' >> $(LOCAL_REPO)/com/onelogin/java-saml-core/maven-metadata-local.xml
	@echo '  </versioning>' >> $(LOCAL_REPO)/com/onelogin/java-saml-core/maven-metadata-local.xml
	@echo '</metadata>' >> $(LOCAL_REPO)/com/onelogin/java-saml-core/maven-metadata-local.xml

	# Toolkit metadata
	@echo '<?xml version="1.0" encoding="UTF-8"?>' > $(LOCAL_REPO)/com/onelogin/java-saml/maven-metadata-local.xml
	@echo '<metadata>' >> $(LOCAL_REPO)/com/onelogin/java-saml/maven-metadata-local.xml
	@echo '  <groupId>com.onelogin</groupId>' >> $(LOCAL_REPO)/com/onelogin/java-saml/maven-metadata-local.xml
	@echo '  <artifactId>java-saml</artifactId>' >> $(LOCAL_REPO)/com/onelogin/java-saml/maven-metadata-local.xml
	@echo '  <versioning>' >> $(LOCAL_REPO)/com/onelogin/java-saml/maven-metadata-local.xml
	@echo '    <release>$(VERSION)</release>' >> $(LOCAL_REPO)/com/onelogin/java-saml/maven-metadata-local.xml
	@echo '    <versions><version>$(VERSION)</version></versions>' >> $(LOCAL_REPO)/com/onelogin/java-saml/maven-metadata-local.xml
	@echo '    <lastUpdated>'$$(date +%Y%m%d%H%M%S)'</lastUpdated>' >> $(LOCAL_REPO)/com/onelogin/java-saml/maven-metadata-local.xml
	@echo '  </versioning>' >> $(LOCAL_REPO)/com/onelogin/java-saml/maven-metadata-local.xml
	@echo '</metadata>' >> $(LOCAL_REPO)/com/onelogin/java-saml/maven-metadata-local.xml

# Full build targets
.PHONY: build
build: clean compile test package ## Full build with tests

.PHONY: build-fast
build-fast: clean compile package ## Fast build without tests
	$(MAKE) package SKIP_TESTS=true

.PHONY: build-local
build-local: clean compile test install-local ## Full build and install to local repository

.PHONY: build-local-fast
build-local-fast: clean compile install-local ## Fast build and install to local repository without tests
	$(MAKE) install-local SKIP_TESTS=true

# Development targets
.PHONY: verify
verify: ## Run Maven verify phase (includes integration tests)
	mvn verify

.PHONY: dependency-tree
dependency-tree: ## Show dependency tree
	mvn dependency:tree

.PHONY: dependency-check
dependency-check: ## Run OWASP dependency check (may fail due to NVD API issues)
	mvn org.owasp:dependency-check-maven:check

.PHONY: versions
versions: ## Display dependency and plugin version updates
	mvn versions:display-dependency-updates versions:display-plugin-updates

# Repository management
.PHONY: list-local-artifacts
list-local-artifacts: ## List artifacts in local repository
	@echo "Artifacts in $(LOCAL_REPO):"
	@find $(LOCAL_REPO) -name "*.jar" -o -name "*.war" -o -name "*.pom" | sort

.PHONY: list-minimal-artifacts
list-minimal-artifacts: ## List only project artifacts in local repository
	@echo "Project artifacts in $(LOCAL_REPO):"
	@find $(LOCAL_REPO) -path "*/com/onelogin/*" \( -name "*.jar" -o -name "*.war" -o -name "*.pom" \) | sort

.PHONY: show-local-repo-size
show-local-repo-size: ## Show size of local repository
	@if [ -d "$(LOCAL_REPO)" ]; then \
		echo "Local repository size:"; \
		du -sh $(LOCAL_REPO); \
	else \
		echo "Local repository $(LOCAL_REPO) does not exist"; \
	fi

# Quick commands
.PHONY: quick-build
quick-build: ## Quick build for development (compile + package, no tests)
	mvn clean compile package -DskipTests=true

.PHONY: quick-install
quick-install: ## Quick install to local repo (no tests)
	mvn clean install -DskipTests=true -Dmaven.repo.local=$(LOCAL_REPO)

.PHONY: quick-minimal
quick-minimal: ## Quick minimal install for Gradle (only project artifacts)
	$(MAKE) install-local-minimal SKIP_TESTS=true

# Distribution targets
.PHONY: dist
dist: clean package create-dist ## Create distribution package with all JARs

.PHONY: dist-full
dist-full: clean test package create-dist ## Create distribution package with full testing

.PHONY: create-dist
create-dist: ## Create distribution directory with all artifacts
	@echo "Creating distribution package..."
	@mkdir -p $(DIST_DIR)/lib
	@mkdir -p $(DIST_DIR)/docs
	@mkdir -p $(DIST_DIR)/samples

	# Copy main JARs
	@find . -name "*.jar" -path "*/target/*" -not -path "*/test-classes/*" -exec cp {} $(DIST_DIR)/lib/ \;
	@find . -name "*.war" -path "*/target/*" -exec cp {} $(DIST_DIR)/samples/ \;

	# Copy documentation
	@cp README.md $(DIST_DIR)/ 2>/dev/null || echo "README.md not found"
	@cp LICENSE* $(DIST_DIR)/ 2>/dev/null || echo "LICENSE not found"
	@cp CHANGELOG* $(DIST_DIR)/ 2>/dev/null || echo "CHANGELOG not found"

	# Create distribution info
	@echo "Java SAML Toolkit Distribution" > $(DIST_DIR)/DISTRIBUTION.txt
	@echo "Version: $(VERSION)" >> $(DIST_DIR)/DISTRIBUTION.txt
	@echo "Build Date: $$(date)" >> $(DIST_DIR)/DISTRIBUTION.txt
	@echo "Jakarta EE Compatible: Yes" >> $(DIST_DIR)/DISTRIBUTION.txt
	@echo "Java Version Required: 11+" >> $(DIST_DIR)/DISTRIBUTION.txt
	@echo "" >> $(DIST_DIR)/DISTRIBUTION.txt
	@echo "Contents:" >> $(DIST_DIR)/DISTRIBUTION.txt
	@echo "- lib/: Main JAR files" >> $(DIST_DIR)/DISTRIBUTION.txt
	@echo "- samples/: Sample WAR files" >> $(DIST_DIR)/DISTRIBUTION.txt
	@echo "- docs/: Documentation" >> $(DIST_DIR)/DISTRIBUTION.txt

	@echo "Distribution created in $(DIST_DIR)/"
	@echo "Contents:"
	@find $(DIST_DIR) -type f | sort

.PHONY: dist-zip
dist-zip: dist ## Create distribution ZIP file
	@echo "Creating distribution ZIP..."
	@cd $(DIST_DIR) && zip -r ../java-saml-toolkit-$(VERSION)-dist.zip .
	@echo "Distribution ZIP created: java-saml-toolkit-$(VERSION)-dist.zip"

.PHONY: dist-tar
dist-tar: dist ## Create distribution TAR.GZ file
	@echo "Creating distribution TAR.GZ..."
	@tar -czf java-saml-toolkit-$(VERSION)-dist.tar.gz -C $(DIST_DIR) .
	@echo "Distribution TAR.GZ created: java-saml-toolkit-$(VERSION)-dist.tar.gz"

.PHONY: dist-both
dist-both: dist-zip dist-tar ## Create both ZIP and TAR.GZ distribution files

# Release preparation
.PHONY: release-prepare
release-prepare: clean test verify ## Prepare for release (full verification)
	@echo "Release preparation complete. All tests passed and verification successful."

.PHONY: release-dist
release-dist: release-prepare dist-both ## Full release build with distribution packages
	@echo "Release distribution packages created:"
	@ls -la java-saml-toolkit-$(VERSION)-dist.*

# Docker-related (if needed in future)
.PHONY: docker-build
docker-build: build-local ## Build Docker image (requires Dockerfile)
	@if [ -f "Dockerfile" ]; then \
		docker build -t java-saml-toolkit:latest .; \
	else \
		echo "Dockerfile not found. Skipping Docker build."; \
	fi

# IDE support
.PHONY: eclipse
eclipse: ## Generate Eclipse project files
	mvn eclipse:eclipse

.PHONY: idea
idea: ## Generate IntelliJ IDEA project files
	mvn idea:idea

# Default target when no target is specified
.DEFAULT_GOAL := help
