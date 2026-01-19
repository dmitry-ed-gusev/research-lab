#!/usr/bin/env python3
# -*- coding=utf-8 -*-

"""
    Simple k8s (kubernetes) client.

    Created:  Dmitrii Gusev, 23.10.2024
    Modified: Dmitrii Gusev, 25.10.2024
"""

from loguru import logger
from kubernetes import client, config

# useful defaults
K8S_CONFIG_FILE = "c:/development/clusters_legacy_kube_config.json"
K8S_CONTEXT = "prod_ext"


def check_context_in_config(config_file: str, context: str) -> bool:
    """Check the specified context exists in the provided k8s config."""


def generate_k8s_config():
    """Generates connection configuration for k8s."""


# starting the script
logger.info("Starting k8s client")

# Configs can be set in Configuration class directly or using helper utility
config.load_kube_config(K8S_CONFIG_FILE)
# print(config.list_kube_config_contexts(CONFIG_FILE))

# Create an object of the `ApiClient` class to communicate with the cluster
api_instance = client.CoreV1Api()

# # Get info about all pods
# print("Listing pods with their IPs:")
# ret = api_instance.list_pod_for_all_namespaces(watch=False)
# for i in ret.items:
#     print("%s\t%s\t%s" % (i.status.pod_ip, i.metadata.namespace, i.metadata.name))

# the end of the script
logger.info("k8s client finished.")
