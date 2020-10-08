import { createSelector } from "reselect";

import { Group, Groups } from "~/domain/models";
import { AppState } from "~/modules";

export const groupsSelector = (state: AppState) => state.groups;

export const getArray = createSelector([groupsSelector], (groups: Groups.Model) =>
  Object.values(groups).map(
    (group: Group.Model): Group.Model => ({
      id: group.id,
      name: group.name,
      thumbnailUrl: group.thumbnailUrl,
      userIds: group.userIds,
      createdAt: group.createdAt,
      updatedAt: group.updatedAt,
    })
  )
);

export const getGroups = createSelector([getArray], (groups: Group.Model[]) => groups);
