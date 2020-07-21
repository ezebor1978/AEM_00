/**
 * Extension of the dialog to limit the rte editor
 *
 * How to use:
 * 1. Add the data attribute cq-dialog-rte-limit with the limit
 *        <granite:data
 *           jcr:primaryType="nt:unstructured"
 *           cq-dialog-rte-limit="200">
 *        </granite:data>
 */

$(window)
    .adaptTo('foundation-registry')
    .register('foundation.validation.validator', {
        selector: "[data-cq-dialog-rte-limit]",
        validate: function(el) {
            var limit = parseInt($(el).attr('data-cq-dialog-rte-limit'));
            var rteContent = el.textContent.trim().length;
            if(!isNaN(limit)) {
                if(rteContent > limit) {
                    return 'The limit is ' + limit + ' characters';
                }
            }
        }
    });
