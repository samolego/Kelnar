const path = require("path");

// Note: webpack runs from Kelnar/build/wasm/packages/composeApp/
// So we need to go back to the project root correctly
const rootDir = path.resolve(__dirname, "../../../../");
const resourcesDir = path.join(rootDir, "build/processedResources/wasmJs/main");
const sourceResourcesDir = path.join(
  rootDir,
  "composeApp/src/wasmJsMain/resources",
);

// Base path configuration
config.output = config.output || {};
config.output.publicPath = "/Kelnar/";

// Configure devServer
config.devServer = config.devServer || {};

// KEY: Static directory - serve our resources from both build and source
config.devServer.static = [
  ...(config.devServer.static || []),
  {
    directory: resourcesDir,
    publicPath: "/Kelnar",
    watch: true,
  },
  // Add source resources directory for development
  {
    directory: sourceResourcesDir,
    publicPath: "/Kelnar",
    watch: true,
  },
];

// SPA fallback to serve index.html for any route
config.devServer.historyApiFallback = {
  rewrites: [
    {
      from: /.*/,
      to: "/Kelnar/index.html",
    },
  ],
};

// Ensure dev middleware uses correct public path
config.devServer.devMiddleware = {
  ...(config.devServer.devMiddleware || {}),
  publicPath: "/Kelnar",
};

// Clean up other settings
config.devServer.client = {
  ...(config.devServer.client || {}),
  logging: "info",
  overlay: true,
};
