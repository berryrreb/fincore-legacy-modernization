#!/usr/bin/env bash
# =============================================================================
# Helper Script: Switch between Java 8 (Legacy) and Java 21 (Modern LTS)
# Compatible with Google Cloud Shell (Debian/Ubuntu), Linux (SDKMAN/apt), & macOS
# Usage:
#   source ./scripts/use-java.sh 8
#   source ./scripts/use-java.sh 21
# =============================================================================

TARGET_VERSION="${1:-21}"

if [[ "$TARGET_VERSION" != "8" && "$TARGET_VERSION" != "21" ]]; then
  echo "Uso: source ./scripts/use-java.sh [8|21]"
  return 1 2>/dev/null || exit 1
fi

echo "Configurando entorno para Java ${TARGET_VERSION}..."

OS_TYPE="$(uname -s)"

if [[ "$OS_TYPE" == "Darwin" ]]; then
  # macOS
  if [[ "$TARGET_VERSION" == "8" ]]; then
    JDK_PATH=$(/usr/libexec/java_home -v 1.8 2>/dev/null || true)
  else
    JDK_PATH=$(/usr/libexec/java_home -v 21 2>/dev/null || true)
  fi

  if [[ -z "$JDK_PATH" && -d "$HOME/.sdkman/candidates/java" ]]; then
    JDK_PATH=$(find "$HOME/.sdkman/candidates/java" -maxdepth 1 -name "${TARGET_VERSION}.*" | sort -V | tail -n 1)
  fi
else
  # Linux / Google Cloud Shell (Debian/Ubuntu)
  for candidate in \
    "/usr/lib/jvm/java-${TARGET_VERSION}-openjdk-amd64" \
    "/usr/lib/jvm/java-1.${TARGET_VERSION}.0-openjdk-amd64" \
    "/usr/lib/jvm/temurin-${TARGET_VERSION}-jdk-amd64" \
    "/usr/lib/jvm/msopenjdk-${TARGET_VERSION}-amd64"; do
    if [[ -d "$candidate" ]]; then
      JDK_PATH="$candidate"
      break
    fi
  done

  if [[ -z "$JDK_PATH" && -d "$HOME/.sdkman/candidates/java" ]]; then
    JDK_PATH=$(find "$HOME/.sdkman/candidates/java" -maxdepth 1 -name "${TARGET_VERSION}.*" | sort -V | tail -n 1)
  fi

  # Si estamos en Cloud Shell / Debian y el JDK aún no está instalado, instalarlo automáticamente
  if [[ -z "$JDK_PATH" ]] && command -v apt-get >/dev/null 2>&1; then
    echo "JDK ${TARGET_VERSION} no encontrado en /usr/lib/jvm. Instalando openjdk-${TARGET_VERSION}-jdk..."
    sudo apt-get update -qq && sudo apt-get install -y "openjdk-${TARGET_VERSION}-jdk"
    if [[ -d "/usr/lib/jvm/java-${TARGET_VERSION}-openjdk-amd64" ]]; then
      JDK_PATH="/usr/lib/jvm/java-${TARGET_VERSION}-openjdk-amd64"
    fi
  fi
fi

if [[ -n "$JDK_PATH" && -d "$JDK_PATH" ]]; then
  export JAVA_HOME="$JDK_PATH"
  export PATH="$JAVA_HOME/bin:$PATH"
  echo "JAVA_HOME configurado en: $JAVA_HOME"
  java -version
else
  echo "No se encontró automáticamente JDK ${TARGET_VERSION}. Verifica tu instalación con 'update-java-alternatives -l' o SDKMAN."
fi
