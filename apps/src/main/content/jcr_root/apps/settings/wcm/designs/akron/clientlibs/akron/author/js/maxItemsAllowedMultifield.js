/**
 * Extension of the dialog to determine the number of items allowed in a multifield input
 *
 * How to use:
 * 1. Add the property 'validation="multifield-max-5"' to the multifield input.
 * 2. Enter the maximum number of items you want e.g. ' validation="multifield-max-5" ' or ' validation="multifield-max-4" '
 *
 * A validation is triggered to prevent the multifield to have more than the number of items determined
 *
 */

$(window)
    .adaptTo('foundation-registry')
    .register('foundation.validation.validator', {
        // check elements that has attribute data-foundation-validation with value starting with "multifield-max"
        selector: "[data-foundation-validation^='multifield-max']",
        validate: function(el) {
            // parse the max number from the attribute value, the value maybe something like "multifield-max-6"
            var validationName = el.getAttribute('data-validation');
            var max = validationName.replace('multifield-max-', '');
            max = parseInt(max);
            // el here is a coral-multifield element
            // see: https://helpx.adobe.com/experience-manager/6-4/sites/developing/using/reference-materials/coral-ui/coralui3/Coral.Multifield.html
            if (el.items.length > max) {
                // items added are more than allowed, return error
                return 'Max allowed items is ' + max;
            }
        }
    });
