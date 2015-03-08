$(function() {
  var index = [
    {label: "multiset.core/->MultiSet", value: "multiset.core.html#ID-GREATERMultiSet"},
    {label: "multiset.core/Multiplicities", value: "multiset.core.html#IDMultiplicities"},
    {label: "multiset.core/cartprod", value: "multiset.core.html#IDcartprod"},
    {label: "multiset.core/intersect", value: "multiset.core.html#IDintersect"},
    {label: "multiset.core/minus", value: "multiset.core.html#IDminus"},
    {label: "multiset.core/multiplicities", value: "multiset.core.html#IDmultiplicities"},
    {label: "multiset.core/multiplicities->multiset", value: "multiset.core.html#IDmultiplicities-GREATERmultiset"},
    {label: "multiset.core/multiplicity", value: "multiset.core.html#IDmultiplicity"},
    {label: "multiset.core/multiset", value: "multiset.core.html#IDmultiset"},
    {label: "multiset.core/multiset?", value: "multiset.core.html#IDmultisetQMARK"},
    {label: "multiset.core/scale", value: "multiset.core.html#IDscale"},
    {label: "multiset.core/subset?", value: "multiset.core.html#IDsubsetQMARK"},
    {label: "multiset.core/sum", value: "multiset.core.html#IDsum"},
    {label: "multiset.core/union", value: "multiset.core.html#IDunion"}  ];
  $('#api-search').autocomplete({
     source: index,
     focus: function(event, ui) {
       event.preventDefault();
     },
     select: function(event, ui) {
       window.open(ui.item.value, '_self');
       ui.item.value = '';
     }
  });
});

