/**
 * Extension of the dialog to determine min and max number of items required and allowed
 *
 * How to use:
 * 1. Add the property 'validation="in-between-3-6"' to the multifield input.
 * 2. Enter the min and max number of items needed. You should add first the min number and then the max number
 * e.g. ' validation="in-between-3-6" ' or ' validation="in-between-2-8" '
 *
 * A validation is triggered to prevent the multifield to have more or less than the number of items determined
 *
 */

$(window)
    .adaptTo('foundation-registry')
    .register('foundation.validation.validator', {
        // check elements that has attribute data-foundation-validation with value starting with "in-between"
        selector: "[data-foundation-validation^='in-between']",
        validate: function(el) {
            // parse the max number from the attribute value, the value maybe something like "in-between-3-6"
            var validationName = el.getAttribute('data-validation');

            var values = validationName.replace('in-between-', '');
            values = values.split("-");
            var min = parseInt(values[0]);
            var max = parseInt(values[1]);

            // el here is a coral-multifield element
            if (el.items.length < min || el.items.length > max) {
                if(el.items.length < min ) {
                    // items added are less than required, return error
                    return 'Min items required is ' + min;
                } else {
                    // items added are more than allowed, return error
                    return 'Max allowed items is ' + max;
                }
            }
        }
    });
