#!/usr/bin/env python
# coding=utf-8

"""
    Extended utility class/module for JIRA.

    Created: Gusev Dmitrii, 07.10.2017
    Modified: Gusev Dmitrii, 09.10.2017
"""

import jira_constants as jconst
from jira_utility_base import JiraUtilityBase, JiraException

# issue link type
IMPLEMENTS = "Implementation"
# issue link outward direction type
IMPLEMENTS_TYPE = "implements / must come before"
# named JQL queries


class JiraUtilityExtended(JiraUtilityBase):

    def __init__(self, config):
        print "JIRAUtilityExtended.__init__() is working. Config [%s]." % config
        super(JiraUtilityExtended, self).__init__(config)

    def execute_option(self, option):
        print "JIRAUtilityExtended.execute_option() is working. Option [%s]." % option
        if not option or not option.strip() or option not in JIRA_OPTIONS:
            raise JiraException("Invalid or empty option provided [%s]!" % option)
        # call corresponding method
        print "Calling method [%s]." % JIRA_OPTIONS[option]
        JIRA_OPTIONS[option](self)

    def print_current_status_report(self, out_file=None):
        print "JIRAUtilityExtended.print_current_status_report() is working."
        # preparing parameters
        team_name = self.config.get(jconst.CONFIG_KEY_TEAM_NAME)
        print "Specified team name: [%s]." % team_name
        team = self.config.get(jconst.CONFIG_KEY_TEAM_MEMBERS % team_name)
        print "Members of team [%s]: %s" % (team_name, team)
        # report header and body
        report = 'Current "In Progress" status'
        for user in team:
            issues = self.get_current_status_for_user(user)
            report += 'Issues "In Progress" for user [{}], count [{}].\n'.format(user, len(issues))
            if len(issues) > 0:
                report += self.get_issues_report(issues).get_string()
                report += '\n\n'
        # print report to console
        print '\n', report
        # write report to output file, if specified
        self.write_report_to_file(report, out_file)

    def print_closed_issues_report(self, days_back=0, out_file=None, simple_report=False):
        """
        Generate and print report "Closed issues by every team member."
        :param days_back: period of time (back in time) for which we need this report (days)
        :param out_file: file to print report to (if needed)
        :param simple_report: if true, only issues counts will be printed (default false)
        """
        print "JIRAUtilityExtended.print_closed_issues_report() is working."
        # preparing parameters
        team_name = self.config.get(jconst.CONFIG_KEY_TEAM_NAME)
        print "Specified team name: [%s]." % team_name
        team = self.config.get(jconst.CONFIG_KEY_TEAM_MEMBERS % team_name)
        print "Members of team [%s]: %s" % (team_name, team)
        # generate report header
        report = '[] Team closed issues report'
        if days_back > 0:
            report += ' (for last {} day(s))'.format(days_back)
        else:
            report += ' (for the whole time)'
        report += '\n\n'
        #  generate report for each user of specified team
        for user in team:
            # search issues
            issues = self.get_all_closed_issues_for_user(user, days_back)
            # add them to report
            report += 'Issues report for user [{}]. Closed count [{}].\n'.format(user, len(issues))
            if len(issues) > 0 and not simple_report:
                report += self.get_issues_report(issues).get_string()
                report += '\n\n'
        # print report to console
        print '\n', report
        # write report to output file, if specified
        self.write_report_to_file(report, out_file)

    def print_sprint_issues_report(self, out_file=None):
        """
        Generate and print report "All named sprint issues."
        :param out_file: file to print report to (if needed)
        """
        print "JIRAUtilityExtended.print_sprint_issues_report() is working."
        sprint = self.config.get(jconst.CONFIG_KEY_SPRINT)
        print "Sprint for issues search [%s]." % sprint
        # generate report header
        report = 'Issues report for {}.\n'.format(sprint)
        report += self.get_issues_report(self.get_all_sprint_issues(sprint)).get_string()
        # print report to console
        print '\n', report
        # write report to output file, if specified
        self.write_report_to_file(report, out_file)

    def add_component_to_sprint_issues(self):
        print "JIRAUtilityExtended.add_component_to_sprint_issues() is working."
        sprint = self.config.get(jconst.CONFIG_KEY_SPRINT)
        # get team, project name and component name
        team_name = self.config.get(jconst.CONFIG_KEY_TEAM_NAME)
        project = self.config.get(jconst.CONFIG_KEY_TEAM_PROJECT % team_name)
        component = self.config.get(jconst.CONFIG_KEY_TEAM_COMPONENT % team_name)
        print "Sprint for adding component: [%s], team [%s], project [%s], component [%s]." \
              % (sprint, team_name, project, component)
        # add component to all found issues
        self.add_component_to_issues(self.get_all_sprint_issues(sprint), project, component)

    def add_label_to_sprint_issues(self):
        print "JIRAUtilityExtended.add_label_to_sprint_issues() is working."
        sprint = self.config.get(jconst.CONFIG_KEY_SPRINT)
        # get team, project name and label name
        team_name = self.config.get(jconst.CONFIG_KEY_TEAM_NAME)
        label = self.config.get(jconst.CONFIG_KEY_TEAM_LABEL % team_name)
        print "Sprint for adding label: [%s], team [%s], label [%s]." % (sprint, team_name, label)
        # add component to all found issues
        self.add_label_to_issues(self.get_all_sprint_issues(sprint), label)

    @staticmethod
    def get_not_implements_tasks(issues):
        print "JIRAUtilityExtended.get_not_implements_tasks() is working. Issues count [{}].".format(len(issues))

        if not issues or len(issues) == 0:
            raise JiraException("Empty issues list!")

        counter = 0
        found_issues = []
        for issue in issues:
            print "\nProcessing issue [%s]." % issue.key
            # if issue has links - check them for target
            if issue.fields.issuelinks:
                is_link_found = False
                for link in issue.fields.issuelinks:
                    # outward links
                    if hasattr(link, "outwardIssue"):
                        outward_issue = link.outwardIssue
                        # debug output, switch it off in case of many output
                        print "\tOutward [{}], link type [{}], out type [{}], in type [{}]."\
                            .format(outward_issue.key, link.type, link.type.outward, link.type.inward)

                        # check issue type
                        if str(link.type) == IMPLEMENTS and str(link.type.outward) == IMPLEMENTS_TYPE:
                            print "Found link! Link: %s" % link.outwardIssue
                            is_link_found = True
                            break  # we've found needed link, don't need to continue cycle

                    # inward links
                    # if hasattr(link, "inwardIssue"):
                    #     inwardIssue = link.inwardIssue
                    #     print("\tInward: " + inwardIssue.key)

                # (AFTER INTERNAL FOR) if outward link "Implements" not found -> add issue to issues list
                if not is_link_found:
                    found_issues.append(issue)

            else:  # issue doesn't have links, add to resulting list
                print "Issue [%s] doesn't have links." % issue.key
                found_issues.append(issue)

            counter += 1
            if counter % jconst.CONST_PROCESSING_STEP_COUNTER == 0:
                print "Processed [%s] issues." % counter


# options with corresponding method name
JIRA_OPTIONS = {
    'teamStatus': JiraUtilityExtended.print_current_status_report,
    'printClosed': JiraUtilityExtended.print_closed_issues_report,
    'sprintIssues': JiraUtilityExtended.print_sprint_issues_report,
    'addComponent': JiraUtilityExtended.add_component_to_sprint_issues,
    'addLabel': JiraUtilityExtended.add_label_to_sprint_issues
}
