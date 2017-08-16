#!/bin/bash

xml_file="$@"
if [[ ! -f "${xml_file}" ]]; then
    echo "Could not find XML File. Usage: $0 <xml-file>" 1>&2
    exit 1
fi

echo -e "Validating Function Catalog: ${xml_file} \n-------------\n"
xmllint --noout --dtdvalid xml/fcat.dtd "${xml_file}"
