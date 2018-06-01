#!/bin/bash
#
# =============================================================================
#   Script for executing data connector utility. With hardcoded cmd line
#   options/parameters (just for initial version).
#
#   Created:  Gusev Dmitrii, 31.05.2018
#   Modified: Gusev Dmitrii
# =============================================================================

${JAVA_HOME}/bin/java -jar dtex-conn-deploy-app.jar -env _virtual --listds --listtables