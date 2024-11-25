import logging

log = logging.getLogger(__name__)


# Really simple naturalsize that is missing from django humanize :(
def naturalsize(count):
    fcount = float(count)
    k = 1024
    m = k * k
    g = m * k

    result: str = ""

    if fcount < k:
        result = str(count) + 'B'
    elif fcount >= k and fcount < m:
        result = str(int(fcount / (k/10.0)) / 10.0) + 'KB'
    elif fcount >= m and fcount < g:
        result = str(int(fcount / (m/10.0)) / 10.0) + 'MB'
    else:
        result = str(int(fcount / (g/10.0)) / 10.0) + 'GB'

    log.debug(f'naturalsize(): initial {str(count)} converted to {result}')
    return result
