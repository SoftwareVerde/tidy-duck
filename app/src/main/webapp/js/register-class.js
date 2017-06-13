
// this function registers class definitions on the window global variable
// so that the class definitions can be referenced anywhere
function registerClassWithGlobalScope(className, classDefinition) {
    if (!window.app) {
        window.app = {};
    }
    window.app[className] = classDefinition;
}
