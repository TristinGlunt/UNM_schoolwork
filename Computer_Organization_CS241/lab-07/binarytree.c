/******************************************/
/*      @author: Tristin Glunt            */
/*      CS 241 - Lab 7                    */
/*      binartree.c                       */    
/*                                        */
/*                                        */
/******************************************/

struct TreeNode *createNode(int data)
{
    struct TreeNode *node = malloc(sizeof(struct TreeNode));
    node->data = data;
    node->left = NULL;
    node->right = NULL;
    return node;
}

struct TreeNode* insertBST(struct TreeNode* root, int data)
{

}

int removeBST(struct TreeNode** rootRef, int data)
{

}

int maxValueBST(struct TreeNode* root)
{

}

int maxDepth(struct TreeNode* root)
{

}

int isBalanced(struct TreeNode* root)
{

}

int isBST(struct TreeNode* root)
{

}

void printTree(struct TreeNode* root)
{

}

void printLeaves(struct TreeNode* root)
{

}

void freeTree(struct TreeNode* root)
{

}