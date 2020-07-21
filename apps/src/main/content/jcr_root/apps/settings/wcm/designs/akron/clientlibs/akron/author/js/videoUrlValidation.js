/**
 * Extension of the dialog to limit the url video to Youtube or Vimeo only
 *
 * How to use:
 * 1. Add the data attribute cq-dialog-rte-limit with the limit
 *        <granite:data
 *           jcr:primaryType="nt:unstructured"
 *           url-allowed="">
 *        </granite:data>
 */

$(window)
    .adaptTo('foundation-registry')
    .register('foundation.validation.validator', {
        selector: "[data-url-allowed]",
        validate: function(el) {
            var content = el.value;
            var regex = new RegExp(/(http:\/\/|https:\/\/|)(player.|www.)?(vimeo\.com|youtu(be\.com|\.be|be\.googleapis\.com))\/(video\/|embed\/|watch\?v=|v\/)?([A-Za-z0-9._%-]*)(\&\S+)?/);
            if(!regex.test(content)){
                return 'Youtube and Vimeo Urls are the only video streaming platforms allowed';
            }
        }
    });
