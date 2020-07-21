$(window)
    .adaptTo('foundation-registry')
    .register('foundation.validation.validator', {
        selector: "[data-cq-dialog-phone-number-validation]",
        validate: function(el) {
            let required = $(el).attr('data-cq-dialog-phone-number-validation') === 'true';
            let value = el.value.trim();
            let regex = /^1-\d\d\d-\d\d\d-\d\d\d\d$/;

            if(!value && required) {
                return 'Please fill out this field.';
            } else if(value && !regex.test(value)) {
                return 'The Phone number has to follow this format: 1-XXX-XXX-XXXX';
            }
        }
    });
