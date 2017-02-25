@org.hibernate.annotations.GenericGenerator(
        name = ID_GENERATOR_NAME,
        strategy = "enhanced-sequence",
        parameters = {
                @org.hibernate.annotations.Parameter(
                        name = "sequence_name",
                        value = ID_GENERATOR_SEQUENCE
                ),
                @org.hibernate.annotations.Parameter(
                        name = "initial_value",
                        value = "13"
                )
        })

package dg.social.crawler.domain;

import static dg.social.crawler.domain.AbstractEntity.ID_GENERATOR_NAME;
import static dg.social.crawler.domain.AbstractEntity.ID_GENERATOR_SEQUENCE;