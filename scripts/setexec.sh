#!/bin/bash
#
# =============================================================================
#   This script sets executable bit (+x) for all python/shell scripts
#   recursively, starting from current directory.
#
#   Created:  Gusev Dmitry, 24.04.2017
#   Modified:
# =============================================================================

# - set +x bit for all python scripts
find . -name '*.py' -type f -print0 | xargs -0 chmod +x
# - set +x bit for all shell scripts
find . -name '*.sh' -type f -print0 | xargs -0 chmod +x
