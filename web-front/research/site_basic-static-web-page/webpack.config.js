const path = require('path');
const fs = require("fs");
const CopyPlugin = require("copy-webpack-plugin");
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const { CleanWebpackPlugin } = require('clean-webpack-plugin');

// return list of HTML templates
function generateHtmlPlugins(templateDir) {
    const templateFiles = fs.readdirSync(path.resolve(__dirname, templateDir));
    return templateFiles.map((item) => {
        const parts = item.split(".");
        const name = parts[0];
        const extension = parts[1];
        return new HtmlWebpackPlugin({
            filename: `${name}.html`,
            template: path.resolve(__dirname, `${templateDir}/${name}.${extension}`),
            inject: false,
        });
    });
}

// loaded HTML plugins
const htmlPlugins = generateHtmlPlugins("./src/html/views");

module.exports = {

    mode: 'development',

    // source map for debug resulting code
    devtool: 'inline-source-map',

    // setup for webpack dev server
    devServer: {
        contentBase: './dist',
    },

    // source js files
    entry: [
        './src/js/index.js', './src/js/print.js'
    ],
        //{
            //index: './src/js/index.js',
            //print: './src/js/print.js',
        //}

    // output js 'transpiled' output
    output: {
        //filename: 'js/[name].bundle.js',
        filename: 'js/bundle.js',
        path: path.resolve(__dirname, 'dist'),
    },

    // setup for webpack plugins
    plugins: [
        //we don't want to remove the index.html file after the incremental build triggered by watch
        new CleanWebpackPlugin({ cleanStaleWebpackAssets: false }),
        //new HtmlWebpackPlugin({
        //    title: 'JavaScript Example / Webpack Getting Started',
        //}),
        new CopyPlugin({
            patterns: [
                {
                    from: "./src/fonts",
                    to: "./fonts",
                },
                {
                    from: "./src/favicon",
                    to: "./favicon",
                },
                {
                    from: "./src/img",
                    to: "./img",
                },
                //{
                //     from: "./src/uploads",
                //     to: "./uploads",
                // },
            ],
        })
    ].concat(htmlPlugins),

    module: {
        rules: [
            {
                // style/css loaders
                test: /\.(css|sass|scss)$/i,
                //use: ['style-loader', 'css-loader'],

                use: [
                    // {
                    //     loader: MiniCssExtractPlugin.loader,
                    //     options: {},
                    // },
                    {
                        loader: "style-loader",
                        options: {},
                    },
                    {
                        loader: "css-loader",
                        options: {
                            sourceMap: true,
                            url: true,
                        },
                    },
                    {
                        loader: "sass-loader",
                        options: {
                            sourceMap: true,
                        },
                    },
                ],

            },

            {
                // images processing - emits resources for css loader
                test: /\.(png|svg|jpg|jpeg|gif)$/i,
                type: 'asset/resource',
                generator: {
                    filename: 'img/[hash][ext][query]'
                }
            },

            {
                // fonts processing - emits resources for css loader
                test: /\.(woff|woff2|eot|ttf|otf)$/i,
                type: 'asset/resource',
                generator: {
                    filename: 'fonts/[hash][ext][query]'
                }
            },

            {
                test: /\.html$/,
                include: path.resolve(__dirname, "src/html/includes"),
                use: ["raw-loader"],
            },
        ],
    },

};