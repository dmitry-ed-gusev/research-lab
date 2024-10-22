#!/usr/bin/env bash
###############################################################################
#
#   Init script for JS / FrontEnd project (simple)
#
#   Created:  Dmitrii Gusev, 01.01.2021
#   Modified: Dmitrii Gusev, 03.01.2021
#
###############################################################################

# -- create directories structure
BASE_SITE_DIR=$1

if [[ -z ${BASE_SITE_DIR} ]]; then
    echo "Provided empty project / module name! ${BASE_SITE_DIR}"
    exit -1
fi

echo "Creating project module: ${BASE_SITE_DIR}"

# -- create directories structure
mkdir -p ${BASE_SITE_DIR}/dist
mkdir -p ${BASE_SITE_DIR}/src/{favicon,html,fonts,img,js,scss,uploads}
mkdir -p ${BASE_SITE_DIR}/src/html/{includes,views}

# -- init node project and generates package.json file
cd ${BASE_SITE_DIR}
npm init

# -- install webpack (the latest) + CLI + dev server and save it as development dependency
npm install webpack webpack-cli webpack-dev-server clean-webpack-plugin del-cli --save-dev

# -- install additional webpack modules - copy/html plugins (development)
npm install copy-webpack-plugin html-webpack-plugin --save-dev

# -- install additional webpack modules - style/css loaders
npm install --save-dev style-loader css-loader mini-css-extract-plugin raw-loader

# -- CSS minimizers / processors
npm install --save-dev postcss-loader postcss sass-loader sass less less-loader node-sass

# --
npm install --save-dev file-loader ref-loader extract-loader

# -- install babel - JavaScript transpiler - development dependency
npm install babel-core babel-preset-env babel-loader --save-dev

# -- install jquery (required by most of sites) - project dependency
npm install jquery --save

# -- go back to the root directory
cd ..
